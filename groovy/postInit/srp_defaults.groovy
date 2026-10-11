// Decay Chain: Scape and Run: Parasites world-creation defaults.
// SRP's "World Settings" screen (Create World -> More World Options) starts from these values, and dedicated
// servers use them directly: difficulty Normal (0 easy, 1 normal, 2 hard = x3 parasite health/damage/armor and 3x faster evolution, 3 impossible = x10).
// Meteorite infection defaults to on via config/srparasites/SRParasitesWorld.cfg ("Meteor Enabled").

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner

try {
    SRPWorldEntitySpawner.choiceNUMBER = 1
} catch (Throwable t) {
    log.error('Could not set the SRP default difficulty: ' + t)
}
