package wolfdogutilities.storage;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import wolfdogutilities.ChopinLogger;
import wolfdogutilities.dogconstants.DogConstants;
import wolfdogutilities.storage.data.WolfTameCountData;

public class WolfTameCountStorage extends SavedData {

    private Map<UUID, WolfTameCountData> map = Maps.newConcurrentMap();
    private int w_deaths;
    
    public WolfTameCountStorage() { }

    public static WolfTameCountStorage get(Level world) {
        if (!(world instanceof ServerLevel)) {
            throw new RuntimeException("Get storage from client :((");
        }

        var overworld = world.getServer().getLevel(Level.OVERWORLD);

        var storage = overworld.getDataStorage();
        return storage.computeIfAbsent(WolfTameCountStorage::load, WolfTameCountStorage::new , DogConstants.WOLF_TC_STORAGE);
    }

    public void incWolfDeath(LivingEntity e) {
        ++this.w_deaths;
        this.setDirty();
    }

    public int getWolfDeath() {
        return this.w_deaths;
    }

    private void setWolfDeath(int deaths) {
        this.w_deaths = deaths;
        this.setDirty();
    }

    public int getWolfCountFor(LivingEntity e) {
        var entry = map.get( e.getUUID() );
        if (entry == null) return 0;
        return entry.getCount();
    }

    public void incWolfCountFor(LivingEntity e) {
        UUID u = e.getUUID();
        if (u==null) return;
        if ( !map.containsKey(u)) { 
            map.compute(u, (x, y) -> new WolfTameCountData(this, e.getUUID(), 0, 0, 0));
            this.setDirty();
        } 

        map.get(u).incCount();
        this.setDirty();
    }

    public void setWolfCountFor(LivingEntity e, int count) {
        UUID u = e.getUUID();
        if (u==null) return;
        if ( !map.containsKey(u)) { 
            map.compute(u, (x, y) -> new WolfTameCountData(this, e.getUUID(), 0, 0, 0));
            this.setDirty();
        } 

        map.get(u).setCount(count);
        this.setDirty();
    }

    public int getWolfBreedCountFor(LivingEntity e) {
        var entry = map.get( e.getUUID() );
        if (entry == null) return 0;
        return entry.getBreedCount();
    }

    public void incWolfBreedCountFor(LivingEntity e) {
        UUID u = e.getUUID();
        if (u==null) return;
        if ( !map.containsKey(u)) { 
            map.compute(u, (x, y) -> new WolfTameCountData(this, e.getUUID(), 0, 0, 0));
            this.setDirty();
        } 

        map.get(u).incBreedCount();
        this.setDirty();
    }

    public void setWolfBreedCountFor(LivingEntity e, int count) {
        UUID u = e.getUUID();
        if (u==null) return;
        if ( !map.containsKey(u)) { 
            map.compute(u, (x, y) -> new WolfTameCountData(this, e.getUUID(), 0, 0, 0));
            this.setDirty();
        } 

        map.get(u).setBreedCount(count);
        this.setDirty();
    }

    public int getDogTrainCountFor(LivingEntity e) {
        var entry = map.get( e.getUUID() );
        if (entry == null) return 0;
        return entry.getTrainCount();
    }

    public void incDogTrainCountFor(LivingEntity e) {
        UUID u = e.getUUID();
        if (u==null) return;
        if ( !map.containsKey(u)) { 
            map.compute(u, (x, y) -> new WolfTameCountData(this, e.getUUID(), 0, 0, 0));
            this.setDirty();
        } 

        map.get(u).incTrainCount();
        this.setDirty();
    }

    public void setDogTrainCountFor(LivingEntity e, int count) {
        UUID u = e.getUUID();
        if (u==null) return;
        if ( !map.containsKey(u)) { 
            map.compute(u, (x, y) -> new WolfTameCountData(this, e.getUUID(), 0, 0, 0));
            this.setDirty();
        } 

        map.get(u).setTrainCount(count);
        this.setDirty();
    }

    public static WolfTameCountStorage load(CompoundTag nbt) {
        WolfTameCountStorage store = new WolfTameCountStorage();
        store.map.clear();

        ListTag list = nbt.getList("WolfCount", 10);

        for (int i = 0; i < list.size(); ++i) {
            CompoundTag x = list.getCompound(i);
            UUID owner = x.getUUID("owner");
            int prevcount = x.getInt("count");
            int prev_bcount = x.getInt("bcount");
            int prev_tcount = x.getInt("tcount");
            WolfTameCountData wd = new WolfTameCountData(store, owner, prevcount, prev_bcount, prev_tcount);
            store.map.put(owner, wd);

        }


        store.setWolfDeath(nbt.getInt("WolfDeaths"));
        

        return store;
        
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (Entry<UUID, WolfTameCountData> entry : this.map.entrySet()) {
            CompoundTag nbtcp = new CompoundTag();
            if(entry.getKey() != null) nbtcp.putUUID("owner", entry.getKey());
            entry.getValue().write(nbtcp);
            list.add(nbtcp);
        }
        nbt.put("WolfCount", list); 
        nbt.putInt("WolfDeaths", this.w_deaths ); 

        return nbt;
    }
}

