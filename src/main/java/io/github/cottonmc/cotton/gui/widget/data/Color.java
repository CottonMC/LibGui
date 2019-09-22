package io.github.cottonmc.cotton.gui.widget.data;

public interface Color {
	public static final Color WHITE = rgb(0xFF_FFFFFF);
	public static final Color BLACK = rgb(0xFF_000000);
	public static final Color RED   = rgb(0xFF_FF0000);
	public static final Color GREEN = rgb(0xFF_00FF00);
	public static final Color BLUE  = rgb(0xFF_0000FF);
	
	public static final Color WHITE_DYE      = rgb(0xFF_F9FFFE);
	public static final Color ORANGE_DYE     = rgb(0xFF_F9801D);
	public static final Color MAGENTA_DYE    = rgb(0xFF_C74EBD);
	public static final Color LIGHT_BLUE_DYE = rgb(0xFF_3AB3DA);
	public static final Color YELLOW_DYE     = rgb(0xFF_FED83D);
	public static final Color LIME_DYE       = rgb(0xFF_80C71F);
	public static final Color PINK_DYE       = rgb(0xFF_F38BAA);
	public static final Color GRAY_DYE       = rgb(0xFF_474F52);
	public static final Color LIGHT_GRAY_DYE = rgb(0xFF_9D9D97);
	public static final Color CYAN_DYE       = rgb(0xFF_169C9C);
	public static final Color PURPLE_DYE     = rgb(0xFF_8932B8);
	public static final Color BLUE_DYE       = rgb(0xFF_3C44AA);
	public static final Color BROWN_DYE      = rgb(0xFF_835432);
	public static final Color GREEN_DYE      = rgb(0xFF_5E7C16);
	public static final Color RED_DYE        = rgb(0xFF_B02E26);
	public static final Color BLACK_DYE      = rgb(0xFF_1D1D21);
	
	Color[] DYE_COLORS = {
			WHITE_DYE,      ORANGE_DYE, MAGENTA_DYE, LIGHT_BLUE_DYE,
			YELLOW_DYE,     LIME_DYE,   PINK_DYE,    GRAY_DYE,
			LIGHT_GRAY_DYE, CYAN_DYE,   PURPLE_DYE,  BLUE_DYE,
			BROWN_DYE,      GREEN_DYE,  RED_DYE,     BLACK_DYE
	};

	/**
	 * Gets an ARGB integer representing this color in the sRGB colorspace.
	 */
	public int toRgb();
	
	
	public static Color rgb(int value) {
		return new RGB(value);
	}
	
	public static Color rgb(int a, int r, int g, int b) {
		return new RGB(a, r, g, b);
	}
	
	public static Color opaqueRgb(int value) {
		return new RGB(value | 0xFF_000000);
	}
	
	public static class RGB implements Color {
		private final int value;
		
		public RGB(int value) {
			this.value = value;
		}
		
		public RGB(int a, int r, int g, int b) {
			value =
				((a & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) <<  8) |
				 (b & 0xFF);
		}
		
		@Override
		public int toRgb() {
			return value;
		}
		
		public int getA() {
			return (value >> 24) & 0xFF;
		}
		
		public int getR() {
			return (value >> 16) & 0xFF;
		}
		
		public int getG() {
			return (value >> 8) & 0xFF;
		}
		
		public int getB() {
			return value & 0xFF;
		}
		
		/** Gets the chroma value, which is related to the length of the vector in projected (hexagonal) space. */
		public int getChroma() {
			int r = getR();
			int g = getG();
			int b = getB();
			
			int max = Math.max(Math.max(r, g), b);
			int min = Math.min(Math.min(r, g), b);
			return max-min;
		}
		
		/** Gets the HSV/HSL Hue, which is the angle around the color hexagon (or circle) */
		public int getHue() {
			float r = getR()/255f;
			float g = getG()/255f;
			float b = getB()/255f;
			
			float max = Math.max(Math.max(r, g), b);
			float min = Math.min(Math.min(r, g), b);
			float chroma = max-min;
			
			if (chroma==0) return 0;
			
			if (max>=r) return
					(int)((((g-b)/chroma) % 6 ) * 60);
			if (max>=g) return
					(int)((((b-r)/chroma) + 2) * 60);
			if (max>=b) return
					(int)((((r-g)/chroma) + 4) * 60);
			
			//Mathematically, we shouldn't ever reach here
			return 0;
		}
		
		/** Gets the HSL Lightness, or average light intensity, of this color */
		public int getLightness() {
			int r = getR();
			int g = getG();
			int b = getB();
			
			int max = Math.max(Math.max(r, g), b);
			int min = Math.min(Math.min(r, g), b);
			return (max+min)/2;
		}
		
		/** Gets the HSL Luma, or perceptual brightness, of this color */
		public int getLuma() {
			float r = getR()/255f;
			float g = getG()/255f;
			float b = getB()/255f;
			
			return (int)(((0.2126f * r) + (0.7152f * g) + (0.0722f * b)) * 255);
		}
		
		/** Gets the HSV Value, which is just the largest component in the color */
		public int getValue() {
			int r = getR();
			int g = getG();
			int b = getB();
			
			return Math.max(Math.max(r, g), b);
		}
		
		/** Gets the saturation for this color based on chrominance and HSV Value */
		public float getHSVSaturation() {
			float v = getValue(); //I don't rescale these to 0..1 because it's just the ratio between them
			if (v==0) return 0;
			float c = getChroma();
			return c/v;
		}
		
		/** Gets the saturation for this color based on chrominance and HSL <em>luma</em>. */
		public float getHSLSaturation() {
			float l = getLuma()/255f; //rescaled here because there's more than just a ratio going on
			if (l==0 || l==1) return 0;
			float c = getChroma()/255f;
			return c / (1 - Math.abs(2*l - 1));
		}
	}
	
	public static class HSL implements Color {
		/** HSL Hue, from 0..1 */
		private float hue;
		/** HSL Saturation, from 0..1 */
		private float sat;
		/** HSL Luma, from 0..1 */
		private float luma;
		
		public int toRgb() {
			float chroma = 1 - (Math.abs(2*luma - 1));
			//TODO: Finish implementing
			return 0;
		}
		
		public float getHue() {
			return hue;
		}
		
		public float getSaturation() {
			return sat;
		}
		
		public float getLuma() {
			return luma;
		}
	}
}
