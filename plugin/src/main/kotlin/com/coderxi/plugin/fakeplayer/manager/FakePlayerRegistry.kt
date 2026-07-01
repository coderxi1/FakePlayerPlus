package com.coderxi.plugin.fakeplayer.manager

import com.coderxi.plugin.fakeplayer.api.entity.FakePlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class FakePlayerRegistry {

    internal val fakeplayers = ConcurrentHashMap<UUID, FakePlayer>()
    internal val fakeplayersByName = ConcurrentHashMap<String, FakePlayer>()
    internal val fakeplayersByOwnerUuids = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    fun register(fp: FakePlayer) {
        fakeplayers[fp.uuid]?.let { oldFp ->
            oldFp.ownerUuids.forEach { oldOwnerId ->
                fakeplayersByOwnerUuids.computeIfPresent(oldOwnerId) { _, fpUuids ->
                    fpUuids.remove(fp.uuid)
                    if (fpUuids.isEmpty()) null else fpUuids
                }
            }
        }
        fakeplayers[fp.uuid] = fp
        fakeplayersByName[fp.name] = fp
        fp.ownerUuids.forEach { ownerId ->
            fakeplayersByOwnerUuids.computeIfAbsent(ownerId) { ConcurrentHashMap.newKeySet() }.add(fp.uuid)
        }
    }

    fun unregister(uuid: UUID) = fakeplayers[uuid]?.let { fp ->
        fakeplayers.remove(fp.uuid)
        fakeplayersByName.remove(fp.name)
        fp.ownerUuids.forEach { ownerId ->
            fakeplayersByOwnerUuids.computeIfPresent(ownerId) { _, fpUuids ->
                fpUuids.remove(fp.uuid)
                if (fpUuids.isEmpty()) null else fpUuids
            }
        }
    }

    fun fakeplayersByOwnerUuid(ownerId: UUID): Collection<FakePlayer> {
        return fakeplayersByOwnerUuids[ownerId]?.mapNotNull { fakeplayers[it] } ?: emptyList()
    }

}