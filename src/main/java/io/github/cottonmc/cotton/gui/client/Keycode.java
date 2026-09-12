package io.github.cottonmc.cotton.gui.client;

import com.mojang.blaze3d.platform.InputConstants;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/// Represents an [SDL keycode][org.lwjgl.sdl.SDLKeycode] using the vanilla fields in [InputConstants].
///
/// These keycodes are returned by [net.minecraft.client.input.InputWithModifiers#shortcutKey()],
/// but not by [`input()`][net.minecraft.client.input.InputWithModifiers#input()]!
///
/// @see <a href="https://wiki.libsdl.org/SDL3/SDL_Keycode">SDL keycode documentation</a>
/// @since 18.0.0
@MagicConstant(intValues = {
	InputConstants.KEYCODE_A,
	InputConstants.KEYCODE_B,
	InputConstants.KEYCODE_C,
	InputConstants.KEYCODE_E,
	InputConstants.KEYCODE_F,
	InputConstants.KEYCODE_L,
	InputConstants.KEYCODE_M,
	InputConstants.KEYCODE_O,
	InputConstants.KEYCODE_R,
	InputConstants.KEYCODE_U,
	InputConstants.KEYCODE_V,
	InputConstants.KEYCODE_W,
	InputConstants.KEYCODE_X,
	InputConstants.KEYCODE_Y,
	InputConstants.KEYCODE_Z,
	InputConstants.KEYCODE_RETURN,
	InputConstants.KEYCODE_NUMPADENTER,
	InputConstants.KEYCODE_PAGEUP,
	InputConstants.KEYCODE_PAGEDOWN,
	InputConstants.KEYCODE_BACKSPACE,
	InputConstants.KEYCODE_UP,
	InputConstants.KEYCODE_DOWN,
	InputConstants.KEYCODE_FORWARD,
	InputConstants.KEYCODE_BACKWARD,
	InputConstants.KEYCODE_LEFT,
	InputConstants.KEYCODE_RIGHT,
	InputConstants.KEYCODE_NUMPAD9,
	InputConstants.KEYCODE_NUMPAD3,
	InputConstants.KEYCODE_DELETE,
	InputConstants.KEYCODE_HOME,
	InputConstants.KEYCODE_END,
	InputConstants.KEYCODE_F5,
	InputConstants.KEYCODE_TAB,
	InputConstants.KEYCODE_LCONTROL,
	InputConstants.KEYCODE_RCONTROL,
	InputConstants.KEYCODE_SPACE
})
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface Keycode {
}
