package waylanderou.toolsforaatogems.item;

import java.util.function.Supplier;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import waylanderou.almostalltheores.item.Items;;

public enum CustomItemTier implements IItemTier {	
	SAPPHIRE(3, 1361, 8.0F, 3.0F, 10, () -> {
		return Ingredient.fromItems(Items.SAPPHIRE);
	}),
	RUBY(3, 1361, 8.0F, 3.0F, 10, () -> {
		return Ingredient.fromItems(Items.RUBY);
	}),
	SPINEL(3, 1100, 8.0F, 3.0F, 10, () -> {
		return Ingredient.fromItems(Items.SPINEL);
	}),
	AMETHYST(2, 900, 6.0F, 2.0F, 12, () -> {
		return Ingredient.fromItems(Items.AMETHYST);
	}),
	JADE(2, 900, 6.0F, 2.0F, 12, () -> {
		return Ingredient.fromItems(Items.JADE);
	}),
	PERIDOT(2, 900, 6.0F, 2.0F, 12, () -> {
		return Ingredient.fromItems(Items.PERIDOT);
	}),
	TOPAZ(3, 1100, 8.0F, 3.0F, 10, () -> {
		return Ingredient.fromItems(Items.TOPAZ);
	}),
	TANZANITE(2, 700, 6.0F, 2.0F, 12, () -> {
		return Ingredient.fromItems(Items.TANZANITE);
	}),
	ONYX(2, 900, 6.0F, 2.0F, 12, () -> {
		return Ingredient.fromItems(Items.ONYX);
	}),
	OPAL(2, 500, 5.0F, 2.0F, 14, () -> {
		return Ingredient.fromItems(Items.OPAL);
	});

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final LazyValue<Ingredient> repairMaterial;

	private CustomItemTier(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
		this.harvestLevel = harvestLevelIn;
		this.maxUses = maxUsesIn;
		this.efficiency = efficiencyIn;
		this.attackDamage = attackDamageIn;
		this.enchantability = enchantabilityIn;
		this.repairMaterial = new LazyValue<>(repairMaterialIn);
	}

	@Override
	public int getMaxUses() {
		return this.maxUses;
	}

	@Override
	public float getEfficiency() {
		return this.efficiency;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public int getHarvestLevel() {
		return this.harvestLevel;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.getValue();
	}

}
