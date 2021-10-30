package io.github.cottonmc.cotton.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
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
	private TextRenderer font;

	private String text = "";
	private int maxLength = 16;
	private boolean editable = true;
	private int tickCount = 0;


	private int disabledColor = 0x707070;
	private int enabledColor = 0xE0E0E0;
	private int suggestionColor = 0x808080;

	private static final int BACKGROUND_COLOR = 0xFF000000;
	private static final int BORDER_COLOR_SELECTED = 0xFFFFFFA0;
	private static final int BORDER_COLOR_UNSELECTED = 0xFFA0A0A0;
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

	@Environment(EnvType.CLIENT)
	@Nullable
	private BackgroundPainter backgroundPainter;

	public WTextField() {
	}

	public WTextField(Text suggestion) {
		this.suggestion = suggestion;
	}

	public void setText(String s) {
		if (this.textPredicate == null || this.textPredicate.test(s)) {
			this.text = (s.length() > maxLength) ? s.substring(0, maxLength) : s;
			if (onChanged != null) onChanged.accept(this.text);
		}
	}

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

	public void setCursorPos(int location) {
		this.cursor = MathHelper.clamp(location, 0, this.text.length());
		scrollCursorIntoView();
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.cursor;
	}

	public void scrollCursorIntoView() {
		if (scrollOffset > cursor) {
			scrollOffset = cursor;
		}
		if (scrollOffset < cursor && font.trimToWidth(text.substring(scrollOffset), width - TEXT_PADDING_X * 2).length() + scrollOffset < cursor) {
			scrollOffset = cursor;
		}

		int rightMostScrollOffset = text.length() - font.trimToWidth(text, width - TEXT_PADDING_X * 2, true).length();
		scrollOffset = Math.min(rightMostScrollOffset, scrollOffset);
	}

	@Nullable
	public String getSelection() {
		if (select < 0) return null;
		if (select == cursor) return null;

		//Tidy some things
		if (select > text.length()) select = text.length();
		if (cursor < 0) cursor = 0;
		if (cursor > text.length()) cursor = text.length();

		int start = Math.min(select, cursor);
		int end = Math.max(select, cursor);

		return text.substring(start, end);
	}

	public boolean isEditable() {
		return this.editable;
	}

	@Environment(EnvType.CLIENT)
	protected void renderBox(MatrixStack matrices, int x, int y) {
		int borderColor = this.isFocused() ? BORDER_COLOR_SELECTED : BORDER_COLOR_UNSELECTED;
		ScreenDrawing.coloredRect(matrices, x - 1, y - 1, width + 2, height + 2, borderColor);
		ScreenDrawing.coloredRect(matrices, x, y, width, height, BACKGROUND_COLOR);
	}

	@Environment(EnvType.CLIENT)
	protected void renderText(MatrixStack matrices, int x, int y, String visibleText) {
		int textColor = this.editable ? this.enabledColor : this.disabledColor;
		this.font.drawWithShadow(matrices, visibleText, x + TEXT_PADDING_X, y + TEXT_PADDING_Y, textColor);
	}

	@Environment(EnvType.CLIENT)
	protected void renderCursor(MatrixStack matrices, int x, int y, String visibleText) {
		if (this.tickCount / 6 % 2 == 0) return;
		if (this.cursor < this.scrollOffset) return;
		if (this.cursor > this.scrollOffset + visibleText.length()) return;
		int cursorOffset = this.font.getWidth(visibleText.substring(0, this.cursor - this.scrollOffset));
		ScreenDrawing.coloredRect(matrices, x + TEXT_PADDING_X + cursorOffset, y + CURSOR_PADDING_Y, 1, CURSOR_HEIGHT, CURSOR_COLOR);
	}

	@Environment(EnvType.CLIENT)
	protected void renderSuggestion(MatrixStack matrices, int x, int y) {
		if (this.suggestion == null) return;
		this.font.drawWithShadow(matrices, this.suggestion, x + TEXT_PADDING_X, y + TEXT_PADDING_Y, this.suggestionColor);
	}

	@Environment(EnvType.CLIENT)
	protected void renderSelection(MatrixStack matrices, int x, int y, String visibleText) {
		if (select == cursor || select == -1) return;

		int textLength = visibleText.length();

		int left = Math.min(cursor, select);
		int right = Math.max(cursor, select);

		if (right < scrollOffset || left > scrollOffset + textLength) return;

		int normalizedLeft = Math.max(scrollOffset, left) - scrollOffset;
		int normalizedRight = Math.min(scrollOffset + textLength, right) - scrollOffset;

		int leftCaret = font.getWidth(visibleText.substring(0, normalizedLeft));
		int selectionWidth = font.getWidth(visibleText.substring(normalizedLeft, normalizedRight));

		invertedRect(matrices, x + TEXT_PADDING_X + leftCaret, y + CURSOR_PADDING_Y, selectionWidth, CURSOR_HEIGHT);
	}

	@Environment(EnvType.CLIENT)
	protected void renderTextField(MatrixStack matrices, int x, int y) {
		if (this.font == null) this.font = MinecraftClient.getInstance().textRenderer;

		String visibleText = font.trimToWidth(this.text.substring(this.scrollOffset), this.width - 2 * TEXT_PADDING_X);
		renderBox(matrices, x, y);
		renderText(matrices, x, y, visibleText);
		if (this.text.isEmpty() && !this.isFocused()) {
			renderSuggestion(matrices, x, y);
		}
		if (this.isFocused()) {
			renderCursor(matrices, x, y, visibleText);
		}
		renderSelection(matrices, x, y, visibleText);
	}

	@Environment(EnvType.CLIENT)
	private void invertedRect(MatrixStack matrices, int x, int y, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		Matrix4f model = matrices.peek().getModel();
		RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		buffer.vertex(model, x, y + height, 0).next();
		buffer.vertex(model, x + width, y + height, 0).next();
		buffer.vertex(model, x + width, y, 0).next();
		buffer.vertex(model, x, y, 0).next();
		buffer.end();
		BufferRenderer.draw(buffer);
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
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
			this.text = this.text.substring(0, max);
			this.onChanged.accept(this.text);
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

	public WTextField setSuggestion(@Nullable String suggestion) {
		this.suggestion = suggestion != null ? new LiteralText(suggestion) : null;
		return this;
	}

	public WTextField setSuggestion(@Nullable Text suggestion) {
		this.suggestion = suggestion;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public WTextField setBackgroundPainter(BackgroundPainter painter) {
		this.backgroundPainter = painter;
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
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		renderTextField(matrices, x, y);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onClick(int x, int y, int button) {
		requestFocus();
		cursor = getCaretPos(this.text, x - TEXT_PADDING_X);
		scrollCursorIntoView();
		return InputResult.PROCESSED;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onCharTyped(char ch) {
		if (this.text.length() < this.maxLength) {
			//snap cursor into bounds if it went astray
			if (cursor < 0) cursor = 0;
			if (cursor > this.text.length()) cursor = this.text.length();

			String before = this.text.substring(0, cursor);
			String after = this.text.substring(cursor, this.text.length());
			this.text = before + ch + after;
			cursor++;
			scrollCursorIntoView();
			if (onChanged != null) onChanged.accept(text);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onKeyPressed(int ch, int key, int modifiers) {
		if (!this.editable) return;

		if (Screen.isCopy(ch)) {
			String selection = getSelection();
			if (selection != null) {
				MinecraftClient.getInstance().keyboard.setClipboard(selection);
			}

			return;
		} else if (Screen.isPaste(ch)) {
			if (select != -1) {
				int a = select;
				int b = cursor;
				if (b < a) {
					int tmp = b;
					b = a;
					a = tmp;
				}
				String before = this.text.substring(0, a);
				String after = this.text.substring(b);

				String clip = MinecraftClient.getInstance().keyboard.getClipboard();
				text = before + clip + after;
				select = -1;
				cursor = (before + clip).length();
			} else {
				String before = this.text.substring(0, cursor);
				String after = this.text.substring(cursor, this.text.length());

				String clip = MinecraftClient.getInstance().keyboard.getClipboard();
				text = before + clip + after;
				cursor += clip.length();
				if (text.length() > this.maxLength) {
					text = text.substring(0, maxLength);
					if (cursor > text.length()) cursor = text.length();
				}
			}
			scrollCursorIntoView();
			if (onChanged != null) onChanged.accept(text);
			return;
		} else if (Screen.isSelectAll(ch)) {
			select = 0;
			cursor = text.length();
			return;
		}

		//System.out.println("Ch: "+ch+", Key: "+key+", Mod: "+modifiers);

		if (modifiers == 0) {
			if (ch == GLFW.GLFW_KEY_DELETE || ch == GLFW.GLFW_KEY_BACKSPACE) {
				if (text.length() > 0 && cursor > 0) {
					if (select >= 0 && select != cursor) {
						int a = select;
						int b = cursor;
						if (b < a) {
							int tmp = b;
							b = a;
							a = tmp;
						}
						String before = this.text.substring(0, a);
						String after = this.text.substring(b);
						text = before + after;
						if (cursor == b) cursor = a;
						select = -1;
					} else {
						String before = this.text.substring(0, cursor);
						String after = this.text.substring(cursor, this.text.length());

						before = before.substring(0, before.length() - 1);
						text = before + after;
						cursor--;
					}

					if (onChanged != null) onChanged.accept(text);
				}
			} else if (ch == GLFW.GLFW_KEY_LEFT) {
				if (select != -1) {
					cursor = Math.min(cursor, select);
					select = -1; //Clear the selection anchor
				} else {
					if (cursor > 0) cursor--;
				}
			} else if (ch == GLFW.GLFW_KEY_RIGHT) {
				if (select != -1) {
					cursor = Math.max(cursor, select);
					select = -1; //Clear the selection anchor
				} else {
					if (cursor < text.length()) cursor++;
				}
			} else {
				//System.out.println("Ch: "+ch+", Key: "+key);
			}
		} else {
			if (modifiers == GLFW.GLFW_MOD_SHIFT) {
				if (ch == GLFW.GLFW_KEY_LEFT) {
					if (select == -1) select = cursor;
					if (cursor > 0) cursor--;
					if (select == cursor) select = -1;
				} else if (ch == GLFW.GLFW_KEY_RIGHT) {
					if (select == -1) select = cursor;
					if (cursor < text.length()) cursor++;
					if (select == cursor) select = -1;
				}
			}
		}
		scrollCursorIntoView();
	}

	@Override
	public void addNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, new TranslatableText(NarrationMessages.TEXT_FIELD_TITLE_KEY, text));

		if (suggestion != null) {
			builder.put(NarrationPart.HINT, new TranslatableText(NarrationMessages.TEXT_FIELD_SUGGESTION_KEY, suggestion));
		}
	}

	/**
	 * From an X offset past the left edge of a TextRenderer.draw, finds out what the closest caret position (division
	 * between letters) is.
	 *
	 * @param s
	 * @param x
	 *
	 * @return
	 */
	@Environment(EnvType.CLIENT)
	public static int getCaretPos(String s, int x) {
		if (x <= 0) return 0;

		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int lastAdvance = 0;
		for (int i = 0; i < s.length() - 1; i++) {
			int advance = font.getWidth(s.substring(0, i + 1));
			int charAdvance = advance - lastAdvance;
			if (x < advance + (charAdvance / 2)) return i + 1;

			lastAdvance = advance;
		}

		return s.length();
	}
}
