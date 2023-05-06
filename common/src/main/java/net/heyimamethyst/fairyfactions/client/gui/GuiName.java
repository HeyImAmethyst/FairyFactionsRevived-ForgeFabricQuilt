package net.heyimamethyst.fairyfactions.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.heyimamethyst.fairyfactions.FairyFactions;
import net.heyimamethyst.fairyfactions.entities.FairyEntity;
import net.heyimamethyst.fairyfactions.proxy.CommonMethods;
import net.heyimamethyst.fairyfactions.util.FairyUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GuiName extends Screen
{
    protected String screenTitle;
    private FairyEntity fairy;
    private int updateCounter;
    private String nameText;

    private Button done;

    public GuiName(FairyEntity fairyEntity)
    {
        super(Component.literal(""));

        FairyFactions.LOGGER.info("GuiName: constructed");

        screenTitle = "Enter custom name or leave blank for default";
        fairy = fairyEntity;
        nameText = "";

        if (fairy != null && fairy.getCustomName() != null)
        {
            nameText = fairy.getCustomName().getString();
        }
    }

    @Override
    public void init()
    {

        done = Button.builder(Component.literal("Done"), (p_96788_) -> { ConfirmName(); })
                .pos((width / 2 - 100) + 50, height / 4 + 120)
                .size(98, 20)
                .build();

        this.addRenderableWidget(done);

        super.init();
    }

    @Override
    public void onClose()
    {
        fairy.setNameEnabled(false);
        super.onClose();
    }

    public void ConfirmName()
    {

        if (fairy != null)
        {
            FairyFactions.LOGGER.info("GuiName.onGuiClosed: isRemote = "+fairy.level.isClientSide);

            if (fairy.level.isClientSide)
            {
                CommonMethods.sendFairyRename(fairy, nameText);
            }
            else
            {
                FairyUtils.nameFairyEntity(fairy, nameText);
            }
        }

        minecraft.setScreen(null);
    }

    public void tick()
    {
        updateCounter++;

        if (fairy == null || !fairy.isAlive())
        {
            minecraft.setScreen(null);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if (pKeyCode == InputConstants.KEY_BACKSPACE)
        {
            if(nameText.length() > 0)
                nameText = nameText.substring(0, nameText.length() - 1);

            return true;
        }
        else
        {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

    }

    @Override
    public boolean charTyped(char c, int i)
    {
        if (i == InputConstants.KEY_ESCAPE)
        {
            minecraft.setScreen(null);
        }

        if (SharedConstants.isAllowedChatCharacter(c) && nameText.length() < 12)
        {
            nameText += c;
        }

        return true;
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(pPoseStack);

        drawCenteredString(pPoseStack, this.font, screenTitle, width / 2, 40, 0xffffff);
        drawCenteredString(pPoseStack, this.font, nameText, width / 2, 100, 0xffffff);

        super.render(pPoseStack, mouseX, mouseY, delta);
    }
}
