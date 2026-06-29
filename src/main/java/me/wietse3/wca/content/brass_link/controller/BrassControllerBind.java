package me.wietse3.wca.content.brass_link.controller;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BrassControllerBind(String frequency, int power) {
    public static final Codec<BrassControllerBind> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("freq").forGetter(BrassControllerBind::frequency),
                    Codec.INT.fieldOf("power").forGetter(BrassControllerBind::power)
            ).apply(instance, BrassControllerBind::new)
    );

    public static final BrassControllerBind EMPTY = new BrassControllerBind("");

    public BrassControllerBind {
        frequency = frequency.strip();
        if (frequency.isEmpty()) {
            power = 15;
        }
    }

    public BrassControllerBind(String frequency) {
        this(frequency, 15);
    }
}
