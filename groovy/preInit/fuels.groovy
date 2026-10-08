// Decay Chain: Forge-fluid bridge for HBM fuels.
// HBM CE keeps its fuels in its own fluid system, so Immersive Vehicles cannot see them.
// These Forge fluids (with buckets) are what vehicle fuel pumps accept; scripts/vehicles.zs converts
// full HBM fuel containers into buckets of them, and config/mtsconfig.json maps them to engine fuels.

content.createFluid('diesel').setColor(0xF2EED5).noBlock().register()
content.createFluid('gasoline').setColor(0x445772).noBlock().register()
content.createFluid('kerosene').setColor(0xFFA5D2).noBlock().register()
