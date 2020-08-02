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
	
		/**
		 * Constructs an RGB object with 100% alpha value (no transparency)
		 * 
		 * @since 2.0.0
		 */
		public RGB(int r, int g, int b) {
			value =
				(0xFF << 24) |
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

		/**
		 * Calculates an interpolated value along the fraction t between 0.0 and 1.0. When t = 1.0, endVal is returned.
		 * Eg.: If this color is black, your endColor is white and t = 0.5 you get gray.
		 *
		 * @param endColor a Color to interpolate with
		 * @param t fraction between 0.0 and 1.0
		 *
		 * @since 2.3.0
		 */
		public RGB interpolate(RGB endColor, double t){
			double a = (endColor.getA() - this.getA()) * t + this.getA();
			double r = (endColor.getR() - this.getR()) * t + this.getR();
			double g = (endColor.getG() - this.getG()) * t + this.getG();
			double b = (endColor.getB() - this.getB()) * t + this.getB();
			return new RGB((int)a, (int)r, (int)g, (int)b);
		}
	}
	
	public static class HSL implements Color {
		/** HSL Hue, from 0..1 */
		private float hue;
		/** HSL Saturation, from 0..1 */
		private float sat;
		/** HSL Luma, from 0..1 */
		private float luma;
		
		/**
		 * 
		 * @param hue   hue angle, between 0 and 1
		 * @param sat   saturation, between 0 and 1
		 * @param luma  luminance, between 0 and 1
		 */
		public HSL(float hue, float sat, float luma) {
			this.hue = hue;
			this.sat = sat;
			this.luma = luma;
		}
		
		public HSL(int rgb) {
			float r = i_f(rgb >> 16);
			float g = i_f(rgb >> 8);
			float b = i_f(rgb);
			
			float max = Math.max(r, Math.max(g, b));
			float min = Math.min(r, Math.min(g, b));
			
			hue = 0;
			if (max==min) {
				hue = 0;
			} else if (max==r) {
				hue = (g-b)/(max-min);
				hue *= 60f/360f;
			} else if (max==g) {
				hue = 2 + (b-r)/(max-min);
				hue *= 60f/360f;
			} else { //max==b
				hue = 4 + (r-g)/(max-min);
				hue *= 60f/360f;
			}
			if (hue<0) hue+=1;
			
			luma = (max+min)/2f;
			
			sat = 0;
			if (max==0) {
				// the saturation of black is zero, special-cased because (0-0)/0 is undefined
			} else if (min==1) {
				// the saturation of white is zero: (1-1)/1, special-cased for kicks I guess
			} else {
				sat = (max-luma) / Math.min(luma, 1-luma);
			}
		}
		
		public int toRgb() {
			float chroma = (1 - (Math.abs(2*luma - 1))) * sat;
			
			int h = (int)(hue*6); h %= 6;
			
			float x = 1f-Math.abs((hue*6 % 2)-1f); x *= chroma;
			float m = luma - (chroma/2);
			
			while (h<0) h+=6;
			if (h>=6) h = h % 6;
			float rf = 0;
			float gf = 0;
			float bf = 0;
			
			switch(h) {
			case 0:
				rf = chroma+m;
				gf = x+m;
				bf = 0+m;
				break;
			case 1:
				rf = x+m;
				gf = chroma+m;
				bf = 0+m;
				break;
			case 2:
				rf = 0+m;
				gf = chroma+m;
				bf = x+m;
				break;
			case 3:
				rf = 0+m;
				gf = x+m;
				bf = chroma+m;
				break;
			case 4:
				rf = x+m;
				gf = 0+m;
				bf = chroma+m;
				break;
			case 5:
				rf = chroma+m;
				gf = 0+m;
				bf = x+m;
			}
			
			int r = f_255(rf);
			int g = f_255(gf);
			int b = f_255(bf);
			
			return 0xFF_000000 | (r << 16) | (g << 8) | b;
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
		
		private static int f_255(float f) {
			int result = (int)(f*255);
			return Math.min(255,Math.max(0, result));
		}
		
		private static float i_f(int i) {
			return (i & 0xFF) / 255f;
		}
	}
	
	public static class LCH implements Color {
		/** HCL Luma, from 0..1 */
		private float luma;
		/** HCL Chroma, from 0..1 */
		private float chroma;
		/** HCL Hue, from 0..1 */
		private float hue;
		
		
		
		public LCH(float luma, float chroma, float hue) {
			this.luma = luma;
			this.chroma = chroma;
			this.hue = hue;
		}
		
		@Override
		public int toRgb() {
			
			
			//Was going to steal Grondag code but then I looked at it
			return 0; //TODO: Implement
		}
		
		public float getHue() {
			return hue;
		}
		
		public float getChroma() {
			return chroma;
		}
		
		public float getLuma() {
			return luma;
		}
	}
}
