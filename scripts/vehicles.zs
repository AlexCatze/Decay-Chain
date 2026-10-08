// Decay Chain: Immersive Vehicles run on HBM refinery fuels (T3 oil processing) and need HBM materials.

// Fuel bridge: a full HBM fuel container + empty bucket -> bucket of the matching Forge fluid (groovy/preInit/fuels.groovy)
recipes.addShapeless("decay_iv_diesel_bucket", <forge:bucketfilled>.withTag({FluidName: "diesel", Amount: 1000}),
    [<ore:container1000diesel>, <minecraft:bucket>]);
recipes.addShapeless("decay_iv_gasoline_bucket", <forge:bucketfilled>.withTag({FluidName: "gasoline", Amount: 1000}),
    [<ore:container1000gasoline>, <minecraft:bucket>]);
recipes.addShapeless("decay_iv_kerosene_bucket", <forge:bucketfilled>.withTag({FluidName: "kerosene", Amount: 1000}),
    [<ore:container1000kerosene>, <minecraft:bucket>]);

// Body plating and benches use HBM steel (T2)
recipes.replaceAllOccurences(<ore:ingotIron>, <ore:plateSteel>, <mts:mtsofficialpack.plating>);
recipes.replaceAllOccurences(<ore:ingotIron>, <ore:plateSteel>, <mts:mts.enginebench>);
recipes.replaceAllOccurences(<ore:ingotIron>, <ore:plateSteel>, <mts:mts.propellerbench>);

// Plastic parts come from HBM rubber; vehicle electronics need an HBM vacuum tube (T3)
recipes.remove(<mts:mtsofficialpack.plastic>);
recipes.addShapeless("decay_iv_plastic", <mts:mtsofficialpack.plastic> * 2, [<ore:itemRubber>, <ore:dustCoal>]);
recipes.replaceAllOccurences(<minecraft:quartz>, <hbm:circuit:0>, <mts:mtsofficialpack.circuit>);
