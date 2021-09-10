package io.github.cottonmc.cotton.gui.jd;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.SimpleDocTreeVisitor;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Taglet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.ElementKindVisitor8;
import javax.lang.model.util.SimpleTypeVisitor8;

public class PropertyTaglet implements Taglet {
	private static final Pattern PROPERTY_METHOD = Pattern.compile("^(.+)Property$");
	private static final String OBSERVABLE_PROPERTY = "io.github.cottonmc.cotton.gui.widget.data.ObservableProperty";
	private DocTrees docTrees;

	@Override
	public void init(DocletEnvironment env, Doclet doclet) {
		docTrees = env.getDocTrees();
	}

	@Override
	public Set<Location> getAllowedLocations() {
		return Set.of(Location.TYPE);
	}

	@Override
	public boolean isInlineTag() {
		return false;
	}

	@Override
	public String getName() {
		return "properties";
	}

	@Override
	public String toString(List<? extends DocTree> tags, Element element) {
		if (!((element.getKind().isClass() || element.getKind().isInterface()) && element instanceof TypeElement type)) {
			throw new IllegalArgumentException("Not a type: " + element);
		}

		List<PropertyEntry> myEntries = scan(type);
		StringBuilder builder = new StringBuilder();

		if (!myEntries.isEmpty()) {
			builder.append("<div class=\"caption\"><span>Properties</span></div>");
			builder.append("<div class=\"summary-table two-column-summary\">");
			builder.append("<div class=\"table-header col-first\">Property</div>");
			builder.append("<div class=\"table-header col-last\">Description</div>");

			for (int i = 0; i < myEntries.size(); i++) {
				PropertyEntry entry = myEntries.get(i);
				String rowClass = (i % 2 == 0) ? "even-row-color" : "odd-row-color";
				builder.append("<div class=\"col-first ").append(rowClass).append("\"><code><span class=\"member-name-link\">");
				builder.append("<a href=\"#").append(entry.name).append("Property()\">").append(entry.name).append("</a>");
				builder.append("</span></code></div>");
				builder.append("<div class=\"col-last ").append(rowClass).append("\">").append(entry.doc).append("</div>");
			}

			builder.append("</div>");
		}

		Map<String, List<PropertyEntry>> inheritedProperties = new LinkedHashMap<>();
		scanParents(type, inheritedProperties);

		inheritedProperties.forEach((name, entries) -> {
			if (!entries.isEmpty()) {
				builder.append("<dt>Properties from <code>").append(name).append("</code></dt>");
				builder.append("<dd>");
				builder.append(entries.stream().map(entry -> "<code>" + entry.name + "</code>").collect(Collectors.joining(", ")));
				builder.append("</dd>");
			}
		});

		return builder.toString();
	}

	private static String getClassName(TypeElement cl) {
		return cl.getQualifiedName().toString();
	}

	private List<PropertyEntry> scan(TypeElement cl) {
		// Java classes don't have LibGui properties (yet? 😳)
		if (getClassName(cl).startsWith("java.")) return List.of();

		return cl.getEnclosedElements().stream()
				.filter(el -> el.getKind() == ElementKind.METHOD)
				.map(el -> (ExecutableElement) el)
				.filter(el -> el.getReturnType().accept(new ObservableTypeFilter(), null))
				.map(el -> {
					Matcher matcher = PROPERTY_METHOD.matcher(el.getSimpleName());

					if (matcher.matches()) {
						String doc = docTrees.getDocCommentTree(el).getFirstSentence().stream()
								.map(tree -> tree.accept(new SimpleDocTreeVisitor<String, Void>() {
									@Override
									public String visitText(TextTree node, Void o) {
										return node.getBody();
									}
								}, null))
								.filter(Objects::nonNull)
								.findAny().orElse("");

						return new PropertyEntry(matcher.group(1), doc);
					}

					return null;
				})
				.filter(Objects::nonNull)
				.sorted()
				.collect(Collectors.toList());
	}

	private void scanParents(TypeElement cl, Map<String, List<PropertyEntry>> inheritedProperties) {
		TypeVisitor<Void, Void> typeVisitor = new SimpleTypeVisitor8<>() {
			@Override
			public Void visitDeclared(DeclaredType t, Void o) {
				return t.asElement().accept(new ElementKindVisitor8<>() {
					@Override
					public Void visitType(TypeElement e, Object o) {
						String fqn = e.getQualifiedName().toString();
						inheritedProperties.put(fqn, scan(e));
						scanParents(e, inheritedProperties);
						return null;
					}
				}, null);
			}
		};

		cl.getSuperclass().accept(typeVisitor, null);
		cl.getInterfaces().forEach(itf -> itf.accept(typeVisitor, null));
	}

	private static final class ObservableTypeFilter extends SimpleTypeVisitor8<Boolean, Void> {
		ObservableTypeFilter() {
			super(false);
		}

		@Override
		public Boolean visitDeclared(DeclaredType t, Void v) {
			Element type = t.asElement();

			if (type.getKind() == ElementKind.CLASS) {
				return ((TypeElement) type).getQualifiedName().contentEquals(OBSERVABLE_PROPERTY);
			}

			return false;
		}
	}

	private record PropertyEntry(String name, String doc) implements Comparable<PropertyEntry> {
		@Override
		public int compareTo(PropertyEntry o) {
			return name.compareTo(o.name);
		}
	}
}
