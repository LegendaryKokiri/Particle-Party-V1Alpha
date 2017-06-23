package Toolbox;

public class Utility {
	public static final int POSITIVE = 1;
	public static final int NEGATIVE = 2;
	
	public static long nanoToMilli(long l) {
		return (long)(l / 1_000_000);
	}
	
	public static double minClamp(double number, double minClamp) {
		if(number < minClamp) return minClamp;
		return number;
	}
	
	public static Vector2d addVectors(Vector2d v1, Vector2d v2) {
		return new Vector2d(v1.x + v2.x, v1.y + v2.y);
	}
	
	public static Vector2d subtractVectors(Vector2d v1, Vector2d v2) {
		return new Vector2d(v1.x - v2.x, v1.y - v2.y);
	}
	
	public static float getVectorLength(Vector2d v) {
		return (float) Math.sqrt((v.x * v.x) + (v.y * v.y));
	}
	
	public static Vector2d setVectorLength(Vector2d v, float length) {
		float currentLength = (float) Math.sqrt((v.x * v.x) + (v.y * v.y));
		if(currentLength == 0) return v;
		
		float scaleFactor = length / currentLength;
		return new Vector2d(v.x * scaleFactor, v.y * scaleFactor);
	}
	
	public static float dotProduct(Vector2d v1, Vector2d v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
	
	public static Vector2d midPoint(Vector2d v1, Vector2d v2) {
		return new Vector2d((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
	}
	
	public static Vector2d clone(Vector2d v) {
		return new Vector2d(v.x, v.y);
	}
	
	public static float sign(float number, int sign) {
		if(sign == POSITIVE) return Math.abs(number);
		else return -Math.abs(number);
	}
}
