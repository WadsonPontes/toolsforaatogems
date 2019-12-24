package waylanderou.toolsforaatogems.item;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ToolItem;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import waylanderou.toolsforaatogems.ToolsConfig;
import waylanderou.toolsforaatogems.sound.Sounds;

public class ScytheItem extends ToolItem {

	protected int range;
	private static final Set<Material> HARVESTABLES = ImmutableSet.of(			
			Material.PLANTS,
			Material.TALL_PLANTS			
			);

	public ScytheItem(float attackDamageIn, float attackSpeedIn, IItemTier tier, Set<Block> effectiveBlocksIn,
			Properties builder) {
		super(attackDamageIn, attackSpeedIn, tier, effectiveBlocksIn, builder);		
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {		
		return 1F;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack scythe, BlockPos pos, PlayerEntity player) {
		World world = player.world;

		if(!world.isRemote) {
			range = ToolsConfig.scytheRadius.get();
			BlockState state = world.getBlockState(pos);

			if (!HARVESTABLES.contains(state.getMaterial())) return false;

			final int x = pos.getX();
			final int y = pos.getY();
			final int z = pos.getZ();

			for (int xPos = x - range; xPos <= x + range; ++xPos) {
				for (int zPos = z - range; zPos <= z + range; ++zPos) {
					BlockPos target = new BlockPos(xPos, y, zPos);
					if (!(xPos == x && zPos == z) && world.getBlockState(target) == state && breakBlock(scythe, world, target, player, HARVESTABLES)) {
					}
				}
			}
			if(ToolsConfig.playScytheSound.get()) {
				((ServerWorld) world).playSound(null, pos, Sounds.SCYTHE_USED, SoundCategory.BLOCKS, 1f, 1f);
			}			
			return super.onBlockStartBreak(scythe, pos, player);
		} else {
			return true;
		}

	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {		
		World world = context.getWorld();
		if(!world.isRemote) {
			ServerWorld serverWorld = (ServerWorld) world;
			BlockPos pos = context.getPos();
			Block block = serverWorld.getBlockState(pos).getBlock();
			int harvestCount = 0;
			PlayerEntity player = context.getPlayer();
			if(block instanceof IPlantable && block instanceof IGrowable && player != null) {
				range = ToolsConfig.scytheRadius.get();
				for (int z = pos.getZ() - range; z <= pos.getZ() + range; ++z) {
					for (int x = pos.getX() - range; x <= pos.getX() + range; ++x) {
						BlockPos target = new BlockPos(x, pos.getY(), z);
						BlockState state = serverWorld.getBlockState(target);
						int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, context.getItem());
						if (harvest(serverWorld, target, state, player, context.getItem(), fortune)) {
							++harvestCount;
						}
					}
				}
				if (harvestCount > 0) {
					int damageAmount;
					if(ToolsConfig.lessDurability.get() == 0) {
						damageAmount = harvestCount;					
					} else {
						damageAmount = ToolsConfig.lessDurability.get();						
					}
					context.getItem().damageItem(damageAmount, player, (onBroken) -> {
						onBroken.sendBreakAnimation(context.getHand());	        		
					});		        			        
					player.addExhaustion(0.02F);
					if(ToolsConfig.playScytheSound.get()) {
						serverWorld.playSound(null, pos, Sounds.SCYTHE_USED, SoundCategory.BLOCKS, 1f, 1f);
					}

					return ActionResultType.SUCCESS;
				}	        
			}
		}		
		return ActionResultType.PASS;
	}

	private boolean harvest(ServerWorld world, BlockPos target, BlockState state, PlayerEntity player, ItemStack tool, int fortune) {
		Block block = state.getBlock();
		if(!(block instanceof IGrowable && block instanceof IPlantable)) {
			return false;			
		}
		IGrowable growable = (IGrowable) block;

		if (!growable.canGrow(world, target, state, world.isRemote)) {			
			NonNullList<ItemStack> drops = NonNullList.create();
			drops.addAll(Block.getDrops(state, world, target, null, player, tool));
			ForgeEventFactory.fireBlockHarvesting(drops, world, target, state, fortune, 1, false, player);			
			boolean foundSeed = false;			
			for (ItemStack drop : drops) {
				Item item = drop.getItem();
				if (!foundSeed && item instanceof BlockItem && ((BlockItem) item).getBlock() == block) {
					foundSeed = true;
					Random random = new Random();
					ItemStack seed = new ItemStack(item, random.nextInt(2)+1); //Only spawn one or two seeds, otherwise it's too OP
					if(ToolsConfig.scytheDropSeeds.get()) {
						Block.spawnAsEntity(world, target, seed);				
					}
				} else {
					Block.spawnAsEntity(world, target, drop);
				}
			}			
			world.setBlockState(target, block.getDefaultState(), 2);
			return true;
		}
		return false;
	}

	private static boolean breakBlock(ItemStack scythe, World world, BlockPos pos, PlayerEntity player, Set<Material> effectiveMaterials) {
		if (world.isAirBlock(pos) || !(player instanceof ServerPlayerEntity)) return false;

		ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
		BlockState state = player.world.getBlockState(pos);
		Block block = state.getBlock();

		if (!effectiveMaterials.contains(state.getMaterial())) return false;

		int xpDropped = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);

		if (xpDropped == -1) return false;		

		if (!world.isRemote) {
			block.onBlockHarvested(world, pos, state, playerMP);

			if (block.removedByPlayer(state, world, pos, playerMP, true, state.getFluidState())) {
				block.onPlayerDestroy(world, pos, state);
				block.harvestBlock(world, player, pos, state, null, scythe);
				block.dropXpOnBlockBreak(world, pos, xpDropped);
			}
			playerMP.connection.sendPacket(new SChangeBlockPacket(world, pos));
		} else {
			world.playEvent(2001, pos, Block.getStateId(state));
			if (block.removedByPlayer(state, world, pos, playerMP, true, state.getFluidState())) {
				block.onPlayerDestroy(world, pos, state);
			}
			scythe.onBlockDestroyed(world, state, pos, playerMP);
		}
		return true;
	}

}
