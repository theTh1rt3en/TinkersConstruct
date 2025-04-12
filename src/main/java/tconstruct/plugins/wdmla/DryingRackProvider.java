package tconstruct.plugins.wdmla;

import com.google.common.collect.Lists;
import com.gtnewhorizons.wdmla.api.accessor.Accessor;
import com.gtnewhorizons.wdmla.api.accessor.BlockAccessor;
import com.gtnewhorizons.wdmla.api.provider.IBlockComponentProvider;
import com.gtnewhorizons.wdmla.api.provider.IClientExtensionProvider;
import com.gtnewhorizons.wdmla.api.provider.IServerDataProvider;
import com.gtnewhorizons.wdmla.api.provider.IServerExtensionProvider;
import com.gtnewhorizons.wdmla.api.provider.ITimeFormatConfigurable;
import com.gtnewhorizons.wdmla.api.ui.IComponent;
import com.gtnewhorizons.wdmla.api.ui.ITooltip;
import com.gtnewhorizons.wdmla.api.view.ClientViewGroup;
import com.gtnewhorizons.wdmla.api.view.ItemView;
import com.gtnewhorizons.wdmla.api.view.ViewGroup;
import com.gtnewhorizons.wdmla.config.WDMlaConfig;
import com.gtnewhorizons.wdmla.impl.format.TimeFormattingPattern;
import com.gtnewhorizons.wdmla.impl.ui.ThemeHelper;
import com.gtnewhorizons.wdmla.impl.ui.component.ItemComponent;
import com.gtnewhorizons.wdmla.impl.ui.component.TextComponent;
import com.gtnewhorizons.wdmla.impl.ui.sizer.Size;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.library.crafting.DryingRackRecipes;

import java.util.Arrays;

public enum DryingRackProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor>, ITimeFormatConfigurable {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor) {
        ItemStack content = ItemStack.loadItemStackFromNBT(accessor.getServerData());
        if(content != null) {
            int time = accessor.getServerData().getInteger("Time");
            int maxTime = accessor.getServerData().getInteger("MaxTime");
            if(time != 0 && maxTime != 0) {
                ItemStack result = DryingRackRecipes.getDryingResult(content);
                if(result != null) {
                    result.stackSize = 0;
                }
                IComponent progressComponent = ThemeHelper.INSTANCE.itemProgress(
                        Arrays.asList(content),
                        Arrays.asList(result),
                        time,
                        maxTime,
                        new TextComponent("TODO"),
                        accessor.showDetails());
                if (progressComponent != null) {
                    tooltip.child(progressComponent);
                }
            }
            else {
                String strippedName = DisplayUtil.stripSymbols(DisplayUtil.itemDisplayNameShort(content));
                TextComponent name = new TextComponent(strippedName);
                int itemSize = name.getHeight();
                tooltip.horizontal().child(
                        new ItemComponent(content).doDrawOverlay(false).size(new Size(itemSize, itemSize)))
                        .text(String.valueOf(content.stackSize)).text("× ").child(name);// TODO: storage view like item display api
            }
        }
    }

    @Override
    public void appendServerData(NBTTagCompound data, BlockAccessor accessor) {
        if (accessor.getTarget() instanceof DryingRackLogic tileEntity) {
            ItemStack stack = tileEntity.getStackInSlot(0);
            if (stack == null) {
                return;
            }
            NBTTagCompound rawData = new NBTTagCompound();
            tileEntity.writeToNBT(rawData);
            stack = stack.copy();
            stack.writeToNBT(data);
            data.setInteger("MaxTime", rawData.getInteger("MaxTime"));
            data.setInteger("Time", rawData.getInteger("Time"));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(TConstruct.modID.toLowerCase(), "drying_rack");
    }

    @Override
    public TimeFormattingPattern getDefaultTimeFormatter() {
        return TimeFormattingPattern.ALWAYS_SECOND;
    }
}
