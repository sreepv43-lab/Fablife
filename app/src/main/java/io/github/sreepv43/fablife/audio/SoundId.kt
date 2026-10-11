package io.github.sreepv43.fablife.audio

/** The generated ambient sounds. [key] is the id used in content (tools/catalog.py, assets/audio.json). */
enum class SoundId(val key: String) {
    WHITE("white"),
    PINK("pink"),
    BROWN("brown"),
    RAIN("rain"),
    OCEAN("ocean"),
    FAN("fan"),
    FOCUS_PAD("focus_pad");

    companion object {
        fun fromKey(key: String?): SoundId? = entries.firstOrNull { it.key == key }
    }
}

/**
 * Endless mono PCM in [-1, 1]. The output depends only on how many samples have been rendered, never on how the
 * calls were split, and rendering never allocates.
 */
interface SoundGenerator {
    val sampleRate: Int

    /** Fills `out[offset until offset + count]` with the next samples. */
    fun render(out: FloatArray, offset: Int = 0, count: Int = out.size - offset)
}

object Soundscapes {
    const val DEFAULT_SAMPLE_RATE = 24_000

    fun create(id: SoundId, sampleRate: Int = DEFAULT_SAMPLE_RATE, seed: Long = 1L): SoundGenerator {
        require(sampleRate in 8_000..192_000) { "sampleRate $sampleRate out of range" }
        return when (id) {
            SoundId.WHITE -> WhiteNoise(sampleRate, seed)
            SoundId.PINK -> PinkNoise(sampleRate, seed)
            SoundId.BROWN -> BrownNoise(sampleRate, seed)
            SoundId.RAIN -> Rain(sampleRate, seed)
            SoundId.OCEAN -> Ocean(sampleRate, seed)
            SoundId.FAN -> Fan(sampleRate, seed)
            SoundId.FOCUS_PAD -> FocusPad(sampleRate, seed)
        }
    }
}
