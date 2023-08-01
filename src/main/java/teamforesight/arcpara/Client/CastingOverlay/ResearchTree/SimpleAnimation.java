package teamforesight.arcpara.Client.CastingOverlay.ResearchTree;

public class SimpleAnimation {


	private final float timer_duration;
	private final boolean loop;
	private float timer_tick;
	private boolean animation_active = false;

	public SimpleAnimation(float duration) {
		this(duration, false);
	}

	public SimpleAnimation(float duration, boolean loop) {
		timer_tick = duration;
		timer_duration = duration;
		this.loop = loop;
	}

	public void start() {
		timer_tick = timer_duration;
		animation_active = true;
	}

	public void tick(float pTick) {
		if (animation_active) {
			if (timer_tick > 0) {
				timer_tick -= pTick;
			} else {
				animation_active = false;
				if (loop) {
					start();
				}
			}
		}
	}

	public void stop() {
		animation_active = false;
		timer_tick = timer_duration;
	}

	public float getPercentageProgress() {
		return 1.0f - timer_tick / timer_duration;
	}

	public float getTicks(){
		return timer_tick;
	}

}
