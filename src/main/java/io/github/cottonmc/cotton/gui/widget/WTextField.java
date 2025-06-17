package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
import io.github.cottonmc.cotton.gui.impl.mixin.client.TextFieldWidgetAccessor;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class WTextField extends WWidget {
	public static final int TEXT_PADDING_X = 4;
	public static final int TEXT_PADDING_Y = 6;
	public static final int CURSOR_PADDING_Y = 4;
	public static final int CURSOR_HEIGHT = 12;

	@Environment(EnvType.CLIENT)
	@Nullable
	private TextRenderer textRenderer;

	private String text = "";
	private int maxLength = 16;
	private boolean editable = true;
	private int tickCount = 0;

	private int disabledColor = 0xFF_707070;
	private int enabledColor = 0xFF_E0E0E0;
	private int suggestionColor = 0xFF_808080;

	private static final int CURSOR_COLOR = 0xFFD0D0D0;

	@Nullable
	private Text suggestion = null;

	// Index of the leftmost character to be rendered.
	private int scrollOffset = 0;

	private int cursor = 0;
	/**
	 * If not -1, select is the "anchor point" of a selection. That is, if you hit shift+left with no existing
	 * selection, the selection will be anchored to where you were, but the cursor will move left, expanding the
	 * selection as you continue to move left. If you move to the right, eventually you'll overtake the anchor, drop the
	 * anchor at the same place and start expanding the selection rightwards instead.
	 */
	private int select = -1;

	private Consumer<String> onChanged;

	private Predicate<String> textPredicate;

	public WTextField() {
	}

	public WTextField(Text suggestion) {
		this.suggestion = suggestion;
	}

	/**
	 * Sets the text of this text field.
	 * If the text is more than the {@linkplain #getMaxLength() max length},
	 * it'll be shortened to the max length.
	 *
	 * @param s the new text
	 */
	public void setText(String s) {
		setTextWithResult(s);
	}

	private boolean setTextWithResult(String s) {
		if (this.textPredicate == null || this.textPredicate.test(s)) {
			this.text = (s.length() > maxLength) ? s.substring(0, maxLength) : s;
			// Call change listener
			if (onChanged != null) onChanged.accept(this.text);
			// Reset cursor if needed
			cursor = clampCursor(cursor);
			return true;
		}

		return false;
	}

	/**
	 * {@return the text in this text field}
	 */
	public String getText() {
		return this.text;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		this.tickCount++;
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}

	private int clampCursor(int cursor) {
		return MathHelper.clamp(cursor, 0, text.length());
	}

	public void setCursorPos(int location) {
		this.cursor = clampCursor(location);
		scrollCursorIntoView();
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.cursor;
	}

	@Environment(EnvType.CLIENT)
	private TextRenderer getTextRenderer() {
		return textRenderer != null ? textRenderer : (textRenderer = MinecraftClient.getInstance().textRenderer);
	}

	@Environment(EnvType.CLIENT)
	public void scrollCursorIntoView() {
		TextRenderer font = getTextRenderer();

		if (scrollOffset > cursor) {
			scrollOffset = cursor;
		}
		if (scrollOffset < cursor && font.trimToWidth(text.substring(scrollOffset), width - TEXT_PADDING_X * 2).length() + scrollOffset < cursor) {
			scrollOffset = cursor;
		}

		checkScrollOffset();
	}

	@Environment(EnvType.CLIENT)
	private void checkScrollOffset() {
		int rightMostScrollOffset = text.length() - getTextRenderer().trimToWidth(text, width - TEXT_PADDING_X * 2, true).length();
		scrollOffset = Math.min(rightMostScrollOffset, scrollOffset);
	}

	@Nullable
	public String getSelection() {
		if (select < 0) return null;
		if (select == cursor) return null;

		//Tidy some things
		if (select > text.length()) select = text.length();
		cursor = clampCursor(cursor);

		int start = Math.min(select, cursor);
		int end = Math.max(select, cursor);

		return text.substring(start, end);
	}

	public boolean isEditable() {
		return this.editable;
	}

	@Environment(EnvType.CLIENT)
	protected void renderBox(DrawContext context, int x, int y) {
		var texture = TextFieldWidgetAccessor.libgui$getTextures().get(isEditable(), isFocused());
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, x - 1, y - 1, width + 2, height + 2);
	}

	@Environment(EnvType.CLIENT)
	protected void renderText(DrawContext context, int x, int y, String visibleText) {
		int textColor = this.editable ? this.enabledColor : this.disabledColor;
		context.drawText(getTextRenderer(), visibleText, x + TEXT_PADDING_X, y + TEXT_PADDING_Y, textColor, true);
	}

	@Environment(EnvType.CLIENT)
	protected void renderCursor(DrawContext context, int x, int y, String visibleText) {
		if (this.tickCount / 6 % 2 == 0) return;
		if (this.cursor < this.scrollOffset) return;
		if (this.cursor > this.scrollOffset + visibleText.length()) return;
		int cursorOffset = getTextRenderer().getWidth(visibleText.substring(0, this.cursor - this.scrollOffset));
		ScreenDrawing.coloredRect(context, x + TEXT_PADDING_X + cursorOffset, y + CURSOR_PADDING_Y, 1, CURSOR_HEIGHT, CURSOR_COLOR);
	}

	@Environment(EnvType.CLIENT)
	protected void renderSuggestion(DrawContext context, int x, int y) {
		if (this.suggestion == null) return;
		context.drawText(getTextRenderer(), suggestion, x + TEXT_PADDING_X, y + TEXT_PADDING_Y, this.suggestionColor, true);
	}

	@Environment(EnvType.CLIENT)
	protected void renderSelection(DrawContext context, int x, int y, String visibleText) {
		if (select == cursor || select == -1) return;

		int textLength = visibleText.length();

		int left = Math.min(cursor, select);
		int right = Math.max(cursor, select);

		if (right < scrollOffset || left > scrollOffset + textLength) return;

		int normalizedLeft = Math.max(scrollOffset, left) - scrollOffset;
		int normalizedRight = Math.min(scrollOffset + textLength, right) - scrollOffset;

		TextRenderer font = getTextRenderer();
		int leftCaret = font.getWidth(visibleText.substring(0, normalizedLeft));
		int selectionWidth = font.getWidth(visibleText.substring(normalizedLeft, normalizedRight));

		drawHighlight(context, x + TEXT_PADDING_X + leftCaret, y + CURSOR_PADDING_Y, selectionWidth, CURSOR_HEIGHT);
	}

	@Environment(EnvType.CLIENT)
	protected void renderTextField(DrawContext context, int x, int y) {
		checkScrollOffset();
		String visibleText = getTextRenderer().trimToWidth(this.text.substring(this.scrollOffset), this.width - 2 * TEXT_PADDING_X);
		renderBox(context, x, y);
		renderText(context, x, y, visibleText);
		if (this.text.isEmpty() && !this.isFocused()) {
			renderSuggestion(context, x, y);
		}
		if (this.isFocused()) {
			renderCursor(context, x, y, visibleText);
		}
		renderSelection(context, x, y, visibleText);
	}

	@Environment(EnvType.CLIENT)
	private void drawHighlight(DrawContext context, int x, int y, int width, int height) {
		context.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x, y, x + width, y + height, 0xFF_0000FF);
	}

	public WTextField setTextPredicate(Predicate<String> predicate_1) {
		this.textPredicate = predicate_1;
		return this;
	}

	public WTextField setChangedListener(Consumer<String> listener) {
		this.onChanged = listener;
		return this;
	}

	public WTextField setMaxLength(int max) {
		this.maxLength = max;
		if (this.text.length() > max) {
			setText(this.text.substring(0, max));
		}
		return this;
	}

	public WTextField setEnabledColor(int col) {
		this.enabledColor = col;
		return this;
	}

	public WTextField setSuggestionColor(int suggestionColor) {
		this.suggestionColor = suggestionColor;
		return this;
	}

	public WTextField setDisabledColor(int col) {
		this.disabledColor = col;
		return this;
	}

	public WTextField setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	@Nullable
	public Text getSuggestion() {
		return suggestion;
	}

	public WTextField setSuggestion(@Nullable Text suggestion) {
		this.suggestion = suggestion;
		return this;
	}

	public boolean canFocus() {
		return true;
	}

	@Override
	public void onFocusGained() {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		renderTextField(context, x, y);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onClick(int x, int y, int button) {
		requestFocus();
		cursor = getCaretPosition(x - TEXT_PADDING_X);
		scrollCursorIntoView();
		return InputResult.PROCESSED;
	}

	@Environment(EnvType.CLIENT)
	public int getCaretPosition(int clickX) {
		if (clickX < 0) return 0;
		int lastPos = 0;
		checkScrollOffset();
		String string = text.substring(scrollOffset);
		TextRenderer font = getTextRenderer();
		for (int i = 0; i < string.length(); i++) {
			int w = font.getWidth(string.charAt(i) + "");
			if (lastPos + w >= clickX) {
				if (clickX - lastPos < w / 2) {
					return i + scrollOffset;
				}
			}
			lastPos += w;
		}
		return string.length();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onCharTyped(char ch) {
		if (!isEditable()) return InputResult.IGNORED;
		insertText(ch + "");
		return InputResult.PROCESSED;
	}

	@Environment(EnvType.CLIENT)
	private void insertText(String toInsert) {
		String before, after;
		if (select != -1 && select != cursor) {
			int left = Math.min(cursor, select);
			int right = Math.max(cursor, select);
			before = this.text.substring(0, left);
			after = this.text.substring(right);
		} else {
			before = this.text.substring(0, cursor);
			after = this.text.substring(cursor);
		}
		if (before.length() + after.length() + toInsert.length() > maxLength) return;
		if (setTextWithResult(before + toInsert + after)) {
			select = -1;
			cursor = (before + toInsert).length();
			scrollCursorIntoView();
		}
	}

	@Environment(EnvType.CLIENT)
	private void copySelection() {
		String selection = getSelection();
		if (selection != null) {
			MinecraftClient.getInstance().keyboard.setClipboard(selection);
		}
	}

	@Environment(EnvType.CLIENT)
	private void paste() {
		String clip = MinecraftClient.getInstance().keyboard.getClipboard();
		insertText(clip);
	}

	@Environment(EnvType.CLIENT)
	private void deleteSelection() {
		int left = Math.min(cursor, select);
		int right = Math.max(cursor, select);
		if (setTextWithResult(text.substring(0, left) + text.substring(right))) {
			select = -1;
			cursor = clampCursor(left);
			scrollCursorIntoView();
		}
	}

	@Environment(EnvType.CLIENT)
	private void delete(int modifiers, boolean backwards) {
		if (select == -1 || select == cursor) {
			select = skipCharacters((GLFW.GLFW_MOD_CONTROL & modifiers) != 0, backwards ? -1 : 1);
		}
		deleteSelection();
	}

	@Environment(EnvType.CLIENT)
	private int skipCharacters(boolean skipMany, int direction) {
		if (direction != -1 && direction != 1) return cursor;
		int position = cursor;
		while (true) {
			position += direction;
			if (position < 0) {
				return 0;
			}
			if (position > text.length()) {
				return text.length();
			}
			if (!skipMany) return position;
			if (position < text.length() && Character.isWhitespace(text.charAt(position))) {
				return position;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public void onDirectionalKey(int direction, int modifiers) {
		if ((GLFW.GLFW_MOD_SHIFT & modifiers) != 0) {
			if (select == -1 || select == cursor) select = cursor;
			cursor = skipCharacters((GLFW.GLFW_MOD_CONTROL & modifiers) != 0, direction);
		} else {
			if (select != -1) {
				cursor = direction < 0 ? Math.min(cursor, select) : Math.max(cursor, select);
				select = -1;
			} else {
				cursor = skipCharacters((GLFW.GLFW_MOD_CONTROL & modifiers) != 0, direction);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onKeyPressed(int ch, int key, int modifiers) {
		if (!isEditable()) return InputResult.IGNORED;

		if (Screen.isCopy(ch)) {
			copySelection();
			return InputResult.PROCESSED;
		} else if (Screen.isPaste(ch)) {
			paste();
			return InputResult.PROCESSED;
		} else if (Screen.isSelectAll(ch)) {
			select = 0;
			cursor = text.length();
			return InputResult.PROCESSED;
		}

		switch (ch) {
			case GLFW.GLFW_KEY_DELETE -> delete(modifiers, false);
			case GLFW.GLFW_KEY_BACKSPACE -> delete(modifiers, true);
			case GLFW.GLFW_KEY_LEFT -> onDirectionalKey(-1, modifiers);
			case GLFW.GLFW_KEY_RIGHT -> onDirectionalKey(1, modifiers);
			case GLFW.GLFW_KEY_HOME, GLFW.GLFW_KEY_UP -> {
				if ((GLFW.GLFW_MOD_SHIFT & modifiers) == 0) {
					select = -1;
				}
				cursor = 0;
			}
			case GLFW.GLFW_KEY_END, GLFW.GLFW_KEY_DOWN -> {
				if ((GLFW.GLFW_MOD_SHIFT & modifiers) == 0) {
					select = -1;
				}
				cursor = text.length();
			}
			default -> {
				return InputResult.IGNORED;
			}
		}
		scrollCursorIntoView();

		return InputResult.PROCESSED;
	}

	@Override
	public void addNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, Text.translatable(NarrationMessages.TEXT_FIELD_TITLE_KEY, text));

		if (suggestion != null) {
			builder.put(NarrationPart.HINT, Text.translatable(NarrationMessages.TEXT_FIELD_SUGGESTION_KEY, suggestion));
		}
	}
}
