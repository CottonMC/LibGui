package io.github.cottonmc.cotton.gui.impl.client;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Crashes the game if a LibGui screen is opened in {@code Item.use/useOnBlock/useOnEntity}.
 */
public final class ItemUseChecker {
	// Setting this property to "true" disables the check.
	private static final String ALLOW_ITEM_USE_PROPERTY = "libgui.allowItemUse";

	// Stack walker instance used to check the caller.
	private static final StackWalker STACK_WALKER =
			StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

	// List of banned item use methods.
	private static final List<Pair<String, MethodType>> ITEM_USE_METHODS = Util.make(new ArrayList<>(), result -> {
		MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

		String hand = "class_1268";
		String actionResult = "class_1269";
		String typedActionResult = "class_1271";
		String livingEntity = "class_1309";
		String playerEntity = "class_1657";
		String itemStack = "class_1799";
		String itemUsageContext = "class_1838";
		String world = "class_1937";

		// use
		result.add(resolveItemMethod(resolver, "method_7836", typedActionResult, world, playerEntity, hand));
		// useOnBlock
		result.add(resolveItemMethod(resolver, "method_7884", actionResult, itemUsageContext));
		// useOnEntity
		result.add(resolveItemMethod(resolver, "method_7847", actionResult, itemStack, playerEntity, livingEntity, hand));
	});

	private static Pair<String, MethodType> resolveItemMethod(MappingResolver resolver, String name, String returnType, String... parameterTypes) {
		// Build intermediary descriptor for resolving the method in the mappings.
		StringBuilder desc = new StringBuilder("(");
		for (String type : parameterTypes) {
			desc.append("Lnet/minecraft/").append(type).append(';');
		}
		desc.append(")Lnet/minecraft/").append(returnType).append(';');

		// Remap the method name.
		String deobfName = resolver.mapMethodName("intermediary", "net.minecraft.class_1792", name, desc.toString());

		// Remap the descriptor types.
		Function<String, Class<?>> getIntermediaryClass = className -> {
			className = resolver.mapClassName("intermediary", "net.minecraft." + className);

			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Could not resolve class net.minecraft." + className, e);
			}
		};
		Class<?>[] paramClasses = Arrays.stream(parameterTypes)
				.map(getIntermediaryClass)
				.toArray(Class[]::new);
		Class<?> returnClass = getIntermediaryClass.apply(returnType);

		// Check that the method actually exists.
		try {
			Item.class.getMethod(deobfName, paramClasses);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not find Item method " + deobfName, e);
		}

		return new Pair<>(deobfName, MethodType.methodType(returnClass, paramClasses));
	}

	/**
	 * Checks whether the specified screen is a LibGui screen opened
	 * from an item usage method.
	 *
	 * @throws CrashException if opening the screen is not allowed
	 */
	public static void checkSetScreen(Screen screen) {
		if (!(screen instanceof CottonScreenImpl cs) || Boolean.getBoolean(ALLOW_ITEM_USE_PROPERTY)) return;

		// Check if this is called via Item.use. If so, crash the game.

		// The calling variant of Item.use[OnBlock|OnEntity].
		// If null, nothing bad happened.
		@Nullable Pair<? extends Class<?>, String> useMethodCaller = STACK_WALKER.walk(s -> s
						.skip(3) // checkSetScreen, setScreen injection, setScreen
						.flatMap(frame -> {
							if (!Item.class.isAssignableFrom(frame.getDeclaringClass())) return Stream.empty();

							return ITEM_USE_METHODS.stream()
									.filter(method -> method.getLeft().equals(frame.getMethodName()) &&
											method.getRight().equals(frame.getMethodType()))
									.map(method -> new Pair<>(frame.getDeclaringClass(), method.getLeft()));
						})
						.findFirst())
				.orElse(null);

		if (useMethodCaller != null) {
			String message = """
						[LibGui] Screens cannot be opened in item use methods. Some alternatives include:
							- Using a packet together with LightweightGuiDescription
							- Using an ItemSyncedGuiDescription
						Setting the screen in item use methods leads to threading issues and
						other potential crashes on both the client and the server.
						If you want to disable this check, set the system property %s to "true"."""
					.formatted(ALLOW_ITEM_USE_PROPERTY);
			var cause = new UnsupportedOperationException(message);
			cause.fillInStackTrace();
			CrashReport report = CrashReport.create(cause, "Opening screen");
			report.addElement("Screen opening details")
					.add("Screen class", screen.getClass().getName())
					.add("GUI description", () -> cs.getDescription().getClass().getName())
					.add("Item class", () -> useMethodCaller.getLeft().getName())
					.add("Involved method", useMethodCaller.getRight());
			throw new CrashException(report);
		}
	}
}
