package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
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
		result.add(resolveItemMethod("use", InteractionResult.class, Level.class, Player.class, InteractionHand.class));
		result.add(resolveItemMethod("useOn", InteractionResult.class, UseOnContext.class));
		result.add(resolveItemMethod("interactLivingEntity", InteractionResult.class, ItemStack.class, Player.class, LivingEntity.class, InteractionHand.class));
	});

	private static Pair<String, MethodType> resolveItemMethod(String name, Class<?> returnType, Class<?>... parameterTypes) {
		// Check that the method exists
		try {
			Item.class.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Could not find Item method " + name, e);
		}

		return new Pair<>(name, MethodType.methodType(returnType, parameterTypes));
	}

	/**
	 * Checks whether the specified screen is a LibGui screen opened
	 * from an item usage method.
	 *
	 * @throws ReportedException if opening the screen is not allowed
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
									.filter(method -> method.getFirst().equals(frame.getMethodName()) &&
											method.getSecond().equals(frame.getMethodType()))
									.map(method -> new Pair<>(frame.getDeclaringClass(), method.getFirst()));
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
			CrashReport report = CrashReport.forThrowable(cause, "Opening screen");
			report.addCategory("Screen opening details")
					.setDetail("Screen class", screen.getClass().getName())
					.setDetail("GUI description", () -> cs.getDescription().getClass().getName())
					.setDetail("Item class", () -> useMethodCaller.getFirst().getName())
					.setDetail("Involved method", useMethodCaller.getSecond());
			throw new ReportedException(report);
		}
	}
}
