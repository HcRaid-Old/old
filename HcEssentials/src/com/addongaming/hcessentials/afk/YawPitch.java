package com.addongaming.hcessentials.afk;

public class YawPitch {
	private float yaw;
	private float pitch;

	public YawPitch(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public boolean matches(YawPitch yp) {
		return yaw == yp.getYaw() && pitch == yp.getPitch();
	}

}
