package mods.betterfoliage.client.resource;

import java.util.Random;

import net.minecraft.util.IIcon;

public class SpriteSetRandom extends SpriteSet {

    protected final Random rng = new Random();

    public SpriteSetRandom(String domain, String prefix, String suffix) {
        this(domain, prefix, suffix, true);
    }

    public SpriteSetRandom(String domain, String prefix, String suffix, boolean selfRegister) {
        super(domain, prefix, suffix, selfRegister);
    }

    public final IIcon getRandomSprite() {
        if (sprites == null || sprites.isEmpty()) {
            return null;
        }

        return sprites.get(rng.nextInt(sprites.size()));
    }

    @Override
    public IIcon getSpriteForCoord(int x, int y, int z, int side) {
        return getRandomSprite();
    }
}
