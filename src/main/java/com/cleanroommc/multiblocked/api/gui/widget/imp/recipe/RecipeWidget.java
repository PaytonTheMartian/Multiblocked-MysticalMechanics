package com.cleanroommc.multiblocked.api.gui.widget.imp.recipe;

import com.cleanroommc.multiblocked.api.capability.IO;
import com.cleanroommc.multiblocked.api.capability.MultiblockCapability;
import com.cleanroommc.multiblocked.api.gui.texture.ColorRectTexture;
import com.cleanroommc.multiblocked.api.gui.texture.IGuiTexture;
import com.cleanroommc.multiblocked.api.gui.texture.ResourceTexture;
import com.cleanroommc.multiblocked.api.gui.widget.imp.ImageWidget;
import com.cleanroommc.multiblocked.api.recipe.Content;
import com.cleanroommc.multiblocked.api.recipe.Recipe;
import com.cleanroommc.multiblocked.api.recipe.RecipeCondition;
import com.cleanroommc.multiblocked.util.Size;
import com.google.common.collect.ImmutableList;
import com.cleanroommc.multiblocked.api.gui.widget.WidgetGroup;
import com.cleanroommc.multiblocked.api.gui.widget.imp.DraggableScrollableWidgetGroup;
import com.cleanroommc.multiblocked.api.gui.widget.imp.LabelWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class RecipeWidget extends WidgetGroup {
    public final Recipe recipe;
    public final DraggableScrollableWidgetGroup inputs;
    public final DraggableScrollableWidgetGroup outputs;

    public RecipeWidget(Recipe recipe, ResourceTexture progress, IGuiTexture background) {
        this(recipe, ProgressWidget.JEIProgress, progress, background);
    }

    public RecipeWidget(Recipe recipe, ResourceTexture progress) {
        this(recipe, ProgressWidget.JEIProgress, progress, new ColorRectTexture(0x1f000000));
    }

    public RecipeWidget(Recipe recipe, DoubleSupplier doubleSupplier, ResourceTexture progress, IGuiTexture background) {
        super(0, 0, 176, 84);
        this.recipe = recipe;
        setClientSideWidget();
        inputs = new DraggableScrollableWidgetGroup(5, 5, 64, 64).setBackground(background);
        outputs = new DraggableScrollableWidgetGroup(176 - 64 - 5, 5, 64, 64).setBackground(background);
        this.addWidget(inputs);
        this.addWidget(outputs);
        String duration = I18n.format("multiblocked.recipe.duration", this.recipe.duration / 20.);
        this.addWidget(new ProgressWidget(doubleSupplier, 78, 27, 20, 20, progress).setHoverTooltip(duration));
        this.addWidget(new LabelWidget(5, 73, duration).setTextColor(0xff000000).setDrop(false));
        if (recipe.text != null) {
            this.addWidget(new LabelWidget(80, 73, recipe.text.getFormattedText()).setTextColor(0xff000000).setDrop(false));
        }
        int index = 0;
        for (Map.Entry<MultiblockCapability<?>, ImmutableList<Content>> entry : recipe.inputs.entrySet()) {
            MultiblockCapability<?> capability = entry.getKey();
            for (Content in : entry.getValue()) {
                inputs.addWidget(capability.createContentWidget().setContent(IO.IN, in, false).setSelfPosition(2 + 20 * (index % 3), 2 + 20 * (index / 3)));
                index++;
            }
        }
        for (Map.Entry<MultiblockCapability<?>, ImmutableList<Content>> entry : recipe.tickInputs.entrySet()) {
            MultiblockCapability<?> capability = entry.getKey();
            for (Content in : entry.getValue()) {
                inputs.addWidget(capability.createContentWidget().setContent(IO.IN, in, true).setSelfPosition(2 + 20 * (index % 3), 2 + 20 * (index / 3)));
                index++;
            }
        }
        if (index > 9) {
            inputs.setSize(new Size(64 + 4, 64));
            inputs.setYScrollBarWidth(4).setYBarStyle(null, new ColorRectTexture(-1));
        }

        index = 0;
        for (Map.Entry<MultiblockCapability<?>, ImmutableList<Content>> entry : recipe.outputs.entrySet()) {
            MultiblockCapability<?> capability = entry.getKey();
            for (Content out : entry.getValue()) {
                outputs.addWidget(capability.createContentWidget().setContent(IO.OUT, out, false).setSelfPosition(2 + 20 * (index % 3), 2 + 20 * (index / 3)));
                index++;
            }
        }
        for (Map.Entry<MultiblockCapability<?>, ImmutableList<Content>> entry : recipe.tickOutputs.entrySet()) {
            MultiblockCapability<?> capability = entry.getKey();
            for (Content out : entry.getValue()) {
                outputs.addWidget(capability.createContentWidget().setContent(IO.OUT, out, true).setSelfPosition(2 + 20 * (index % 3), 2 + 20 * (index / 3)));
                index++;
            }
        }
        if (index > 9) {
            outputs.setSize(new Size(64 + 4, 64));
            outputs.setYScrollBarWidth(4).setYBarStyle(null, new ColorRectTexture(-1));
        }
        Map<String, List<RecipeCondition>> conditionMap = new HashMap<>();
        for (RecipeCondition condition : recipe.conditions) {
            if (condition.isReverse()) {
                conditionMap.computeIfAbsent(condition.getType(), s->new ArrayList<>()).add(condition);
            } else {
                conditionMap.computeIfAbsent(condition.getType(), s->new ArrayList<>()).add(0, condition);
            }
        }

        index = 0;
        for (Map.Entry<String, List<RecipeCondition>> entry : conditionMap.entrySet()) {
            List<RecipeCondition> list = entry.getValue();
            if (!list.isEmpty()) {
                index++;
                boolean reversed = false;
                List<ITextComponent> components = new ArrayList<>();
                for (RecipeCondition condition : list) {
                    if (!reversed && condition.isReverse()) {
                        reversed = true;
                        components.add(new TextComponentTranslation("multiblocked.gui.condition.reverse"));
                    }
                    components.add(condition.getTooltips());
                }
                this.addWidget(new ImageWidget(168 - index * 16, 70, 16, 16, list.get(0).getValidTexture()).setHoverTooltip(components.stream().reduce(new TextComponentString(""), ITextComponent::appendSibling).getFormattedText()));
            }

        }
    }
}
