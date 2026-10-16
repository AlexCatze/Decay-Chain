// Decay Chain: Scape and Run: Parasites world-creation defaults.
// SRP's "World Settings" screen (Create World -> More World Options) starts from these values, and dedicated
// servers use them directly: difficulty Normal (0 easy, 1 normal, 2 hard = x3 parasite health/damage/armor and 3x faster evolution, 3 impossible = x10).
// The difficulty is saved per world when it is created: worlds created on Hard stay Hard.
// Meteorites and their per-meteorite evolution (hives): srp_meteor.groovy and the fix jar (decaychain.fixes.hive).

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner

try {
    SRPWorldEntitySpawner.choiceNUMBER = 1
} catch (Throwable t) {
    log.error('Could not set the SRP default difficulty: ' + t)
}
