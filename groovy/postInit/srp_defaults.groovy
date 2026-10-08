// Decay Chain: Scape and Run: Parasites world-creation defaults.
// SRP's "World Settings" screen (Create World -> More World Options) starts from these values, and dedicated
// servers use them directly: difficulty Hard (0 easy, 1 normal, 2 hard, 3 impossible).
// Meteorite infection defaults to on via config/srparasites/SRParasitesWorld.cfg ("Meteor Enabled").

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner

try {
    SRPWorldEntitySpawner.choiceNUMBER = 2
} catch (Throwable t) {
    log.error('Could not set the SRP default difficulty: ' + t)
}
