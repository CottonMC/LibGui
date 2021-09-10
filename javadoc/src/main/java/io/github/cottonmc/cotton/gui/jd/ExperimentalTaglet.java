package io.github.cottonmc.cotton.gui.jd;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.Taglet;

import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;

public class ExperimentalTaglet implements Taglet {
	@Override
	public Set<Location> getAllowedLocations() {
		return Set.of(Location.values());
	}

	@Override
	public boolean isInlineTag() {
		return false;
	}

	@Override
	public String getName() {
		return "experimental";
	}

	@Override
	public String toString(List<? extends DocTree> tags, Element element) {
		return "<dt>Experimental API:</dt><dd>Might be modified or removed without prior notice until stabilised.</dd>";
	}
}
