package io.github.cottonmc.cotton.gui.widget;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class WTextField extends WWidget {
	
	protected String text = "";
	protected int maxLength = 16;
	protected int focusedTicks = 0;
	protected boolean focused = false;
	protected boolean editable = true;
	
	protected int enabledColor = 0xE0E0E0;
	protected int uneditableColor = 0x707070;
	
	@Nullable
	protected String suggestion = null;
	
	protected int cursor = 0;
	
	protected Consumer<String> onChanged;
	
	protected Predicate<String> textPredicate;
	
	@Environment(EnvType.CLIENT)
	@Nullable
	protected BackgroundPainter backgroundPainter;

	public WTextField() {
	}
	
	public WTextField(Text suggestion) {
		this.suggestion = suggestion.asString();
	}
	
	public void setText(String s) {
		if (this.textPredicate.test(s)) {
			this.text = (s.length()>maxLength) ? s.substring(0,maxLength) : s;
			if (onChanged!=null) onChanged.accept(this.text);
		}
	}

	public String getText() {
		return this.text;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	/*
	public String getSelectedText() {
		int start = this.cursorMax < this.cursorMin ? this.cursorMax : this.cursorMin;
		int end = this.cursorMax < this.cursorMin ? this.cursorMin : this.cursorMax;
		return this.text.substring(start, end);
	}*/

	/*
	//This would seem to insert or paste text in the vanilla control
	public void addText(String string_1) {
		String string_2 = "";
		String string_3 = SharedConstants.stripInvalidChars(string_1);
		int int_1 = this.cursorMax < this.cursorMin ? this.cursorMax : this.cursorMin;
		int int_2 = this.cursorMax < this.cursorMin ? this.cursorMin : this.cursorMax;
		int int_3 = this.maxLength - this.text.length() - (int_1 - int_2);
		if (!this.text.isEmpty()) {
			string_2 = string_2 + this.text.substring(0, int_1);
		}

		int int_5;
		if (int_3 < string_3.length()) {
			string_2 = string_2 + string_3.substring(0, int_3);
			int_5 = int_3;
		} else {
			string_2 = string_2 + string_3;
			int_5 = string_3.length();
		}

		if (!this.text.isEmpty() && int_2 < this.text.length()) {
			string_2 = string_2 + this.text.substring(int_2);
		}

		if (this.textPredicate.test(string_2)) {
			this.text = string_2;
			this.setCursor(int_1 + int_5);
			this.method_1884(this.cursorMax);
			this.onChanged(this.text);
		}
	}*/
	
	/*

	//this would seem to delete text. if int_1 is negative, deletes from the end?
	public void method_1878(int int_1) {
		if (!this.text.isEmpty()) {
			if (this.cursorMin != this.cursorMax) {
				this.addText("");
			} else {
				boolean boolean_1 = int_1 < 0;
				int int_2 = boolean_1 ? this.cursorMax + int_1 : this.cursorMax;
				int int_3 = boolean_1 ? this.cursorMax : this.cursorMax + int_1;
				String string_1 = "";
				if (int_2 >= 0) {
					string_1 = this.text.substring(0, int_2);
				}

				if (int_3 < this.text.length()) {
					string_1 = string_1 + this.text.substring(int_3);
				}

				if (this.textPredicate.test(string_1)) {
					this.text = string_1;
					if (boolean_1) {
						this.moveCursor(int_1);
					}

					this.onChanged(this.text);
				}
			}
		}
	}*/

	public void setCursorPos(int location) {
		this.cursor = MathHelper.clamp(location, 0, this.text.length());
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.cursor;
	}
	
	public boolean isEditable() {
		return this.editable;
	}
	
	/*
	public boolean keyPressed(int int_1, int int_2, int int_3) {
		if (!this.method_20315()) {
			return false;
		} else {
			this.field_17037 = Screen.hasShiftDown();
			if (Screen.isSelectAll(int_1)) {
				this.method_1872();
				this.method_1884(0);
				return true;
			} else if (Screen.isCopy(int_1)) {
				MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
				return true;
			} else if (Screen.isPaste(int_1)) {
				if (this.editable) {
					this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
				}

				return true;
			} else if (Screen.isCut(int_1)) {
				MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
				if (this.editable) {
					this.addText("");
				}

				return true;
			} else {
				switch(int_1) {
				case 259:
					if (this.editable) {
						this.method_16873(-1);
					}

					return true;
				case 260:
				case 264:
				case 265:
				case 266:
				case 267:
				default:
					return false;
				case 261:
					if (this.editable) {
						this.method_16873(1);
					}

					return true;
				case 262:
					if (Screen.hasControlDown()) {
						this.method_1883(this.method_1853(1));
					} else {
						this.moveCursor(1);
					}

					return true;
				case 263:
					if (Screen.hasControlDown()) {
						this.method_1883(this.method_1853(-1));
					} else {
						this.moveCursor(-1);
					}

					return true;
				case 268:
					this.method_1870();
					return true;
				case 269:
					this.method_1872();
					return true;
				}
			}
		}
	}*/
	
	/*
	public boolean charTyped(char char_1, int int_1) {
		if (!this.method_20315()) {
			return false;
		} else if (SharedConstants.isValidChar(char_1)) {
			if (this.editable) {
				this.addText(Character.toString(char_1));
			}

			return true;
		} else {
			return false;
		}
	}*/
	/*
	@Override
	public void onClick(int x, int y, int button) {
		
	}
	
	public boolean mouseClicked(double double_1, double double_2, int int_1) {
		if (!this.isVisible()) {
			return false;
		} else {
			boolean boolean_1 = double_1 >= (double)this.x && double_1 < (double)(this.x + this.width) && double_2 >= (double)this.y && double_2 < (double)(this.y + this.height);
			if (this.field_2096) {
				this.method_1876(boolean_1);
			}

			if (this.isFocused() && boolean_1 && int_1 == 0) {
				int int_2 = MathHelper.floor(double_1) - this.x;
				if (this.focused) {
					int_2 -= 4;
				}

				String string_1 = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), this.method_1859());
				this.method_1883(this.textRenderer.trimToWidth(string_1, int_2).length() + this.field_2103);
				return true;
			} else {
				return false;
			}
		}
	}*/

	/*
	public void renderButton(int int_1, int int_2, float float_1) {
		if (this.isVisible()) {
			if (this.hasBorder()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int int_3 = this.editable ? this.editableColor : this.uneditableColor;
			int int_4 = this.cursorMax - this.field_2103;
			int int_5 = this.cursorMin - this.field_2103;
			String string_1 = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), this.method_1859());
			boolean boolean_1 = int_4 >= 0 && int_4 <= string_1.length();
			boolean boolean_2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && boolean_1;
			int int_6 = this.focused ? this.x + 4 : this.x;
			int int_7 = this.focused ? this.y + (this.height - 8) / 2 : this.y;
			int int_8 = int_6;
			if (int_5 > string_1.length()) {
				int_5 = string_1.length();
			}

			if (!string_1.isEmpty()) {
				String string_2 = boolean_1 ? string_1.substring(0, int_4) : string_1;
				int_8 = this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string_2, this.field_2103), (float)int_6, (float)int_7, int_3);
			}

			boolean boolean_3 = this.cursorMax < this.text.length() || this.text.length() >= this.getMaxLength();
			int int_9 = int_8;
			if (!boolean_1) {
				int_9 = int_4 > 0 ? int_6 + this.width : int_6;
			} else if (boolean_3) {
				int_9 = int_8 - 1;
				--int_8;
			}

			if (!string_1.isEmpty() && boolean_1 && int_4 < string_1.length()) {
				this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string_1.substring(int_4), this.cursorMax), (float)int_8, (float)int_7, int_3);
			}

			if (!boolean_3 && this.suggestion != null) {
				this.textRenderer.drawWithShadow(this.suggestion, (float)(int_9 - 1), (float)int_7, -8355712);
			}

			int var10002;
			int var10003;
			if (boolean_2) {
				if (boolean_3) {
					int var10001 = int_7 - 1;
					var10002 = int_9 + 1;
					var10003 = int_7 + 1;
					this.textRenderer.getClass();
					DrawableHelper.fill(int_9, var10001, var10002, var10003 + 9, -3092272);
				} else {
					this.textRenderer.drawWithShadow("_", (float)int_9, (float)int_7, int_3);
				}
			}

			if (int_5 != int_4) {
				int int_10 = int_6 + this.textRenderer.getStringWidth(string_1.substring(0, int_5));
				var10002 = int_7 - 1;
				var10003 = int_10 - 1;
				int var10004 = int_7 + 1;
				this.textRenderer.getClass();
				this.method_1886(int_9, var10002, var10003, var10004 + 9);
			}

		}
	}*/

	/*
	private void method_1886(int int_1, int int_2, int int_3, int int_4) {
		int int_6;
		if (int_1 < int_3) {
			int_6 = int_1;
			int_1 = int_3;
			int_3 = int_6;
		}

		if (int_2 < int_4) {
			int_6 = int_2;
			int_2 = int_4;
			int_4 = int_6;
		}

		if (int_3 > this.x + this.width) {
			int_3 = this.x + this.width;
		}

		if (int_1 > this.x + this.width) {
			int_1 = this.x + this.width;
		}

		Tessellator tessellator_1 = Tessellator.getInstance();
		BufferBuilder bufferBuilder_1 = tessellator_1.getBufferBuilder();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder_1.begin(7, VertexFormats.POSITION);
		bufferBuilder_1.vertex((double)int_1, (double)int_4, 0.0D).next();
		bufferBuilder_1.vertex((double)int_3, (double)int_4, 0.0D).next();
		bufferBuilder_1.vertex((double)int_3, (double)int_2, 0.0D).next();
		bufferBuilder_1.vertex((double)int_1, (double)int_2, 0.0D).next();
		tessellator_1.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}*/

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
	
	public WTextField setDisabledColor(int col) {
		this.uneditableColor = col;
		return this;
	}
	
	public WTextField setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}
	
	public WTextField setSuggestion(@Nullable String suggestion) {
		this.suggestion = suggestion;
		return this;
	}
	
	public WTextField setSuggestion(@Nullable Text suggestion) {
		this.suggestion = suggestion.asString();
		return this;
	}
	
	@Environment(EnvType.CLIENT)
	public WTextField setBackgroundPainter(BackgroundPainter painter) {
		this.backgroundPainter = painter;
		return this;
	}
	/*
	public void method_1884(int int_1) {
		int int_2 = this.text.length();
		this.cursorMin = MathHelper.clamp(int_1, 0, int_2);
		if (this.textRenderer != null) {
			if (this.field_2103 > int_2) {
				this.field_2103 = int_2;
			}

			int int_3 = this.method_1859();
			String string_1 = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), int_3);
			int int_4 = string_1.length() + this.field_2103;
			if (this.cursorMin == this.field_2103) {
				this.field_2103 -= this.textRenderer.trimToWidth(this.text, int_3, true).length();
			}

			if (this.cursorMin > int_4) {
				this.field_2103 += this.cursorMin - int_4;
			} else if (this.cursorMin <= this.field_2103) {
				this.field_2103 -= this.field_2103 - this.cursorMin;
			}

			this.field_2103 = MathHelper.clamp(this.field_2103, 0, int_2);
		}

	}*/

	
	
	/*
	@Environment(EnvType.CLIENT)
	public int getCharacterX(int int_1) {
		return int_1 > this.text.length() ? this.x : this.x + this.textRenderer.getStringWidth(this.text.substring(0, int_1));
	}*/
	
	public boolean canFocus() {
		return true;
	}
	
	@Override
	public void onFocusGained() {
	}
	
	@Override
	public void paintBackground(int x, int y) {
		if (isFocused()) {
			ScreenDrawing.rect(x-1, y-1, this.getWidth()+2, this.getHeight()+2, 0xFFFFFFFF);
		}
		
		if (backgroundPainter!=null) {
			backgroundPainter.paintBackground(x, y, this);
		} else {
			ScreenDrawing.drawBeveledPanel(x, y, this.getWidth(), this.getHeight());
		}
		
		ScreenDrawing.drawString(this.text, x+2, y+6, 0xFFFFFFFF);
		int ofs = MinecraftClient.getInstance().textRenderer.getStringWidth(this.text);
		ScreenDrawing.rect(x+2+ofs, y+4, 1, 12, 0xFFE0E0E0);
	}
	
	@Override
	public void onClick(int x, int y, int button) {
		requestFocus();
	}
	
	@Override
	public void onCharTyped(char ch) {
		if (this.text.length()<this.maxLength) this.text += ch;
	}
	
	@Override
	public void onKeyPressed(int key, int modifiers) {
		if (key==22) {
			if (text.length()>0) text = text.substring(0, text.length()-1);
		}
	}
}
