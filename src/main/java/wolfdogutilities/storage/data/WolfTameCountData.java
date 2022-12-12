package wolfdogutilities.storage.data;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import wolfdogutilities.storage.WolfTameCountStorage;

public class WolfTameCountData {
    private final WolfTameCountStorage ws;
    private final UUID owner;
    private int count;
    private int breed_count;
    private int train_count;
    
    public WolfTameCountData(WolfTameCountStorage ws, UUID owner, int prevcount, int prevbreed_count, int orevtrain_count) {
        this.ws = ws;
        this.owner = owner;
        this.count = prevcount;
        this.breed_count = prevbreed_count;
        this.train_count = train_count;
    }

    public void incCount() {
        ++this.count;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incBreedCount() {
        ++this.breed_count;
    }

    public int getBreedCount() {
        return this.breed_count;
    }

    public void setBreedCount(int bcount) {
        this.breed_count = bcount;
    }

    public void incTrainCount() {
        ++this.train_count;
    }

    public int getTrainCount() {
        return this.train_count;
    }

    public void setTrainCount(int tcount) {
        this.train_count = tcount;
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putInt("count", this.count);
        compound.putInt("bcount", this.breed_count);
        compound.putInt("tcount", this.train_count);
        return compound;
    }
}
