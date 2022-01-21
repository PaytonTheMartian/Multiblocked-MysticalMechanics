package io.github.cleanroommc.multiblocked.gui.widget.imp.controller.structure;

import io.github.cleanroommc.multiblocked.Multiblocked;
import io.github.cleanroommc.multiblocked.api.definition.ControllerDefinition;
import io.github.cleanroommc.multiblocked.gui.texture.ResourceTexture;
import io.github.cleanroommc.multiblocked.gui.widget.WidgetGroup;
import io.github.cleanroommc.multiblocked.gui.widget.imp.tab.TabButton;
import io.github.cleanroommc.multiblocked.gui.widget.imp.tab.TabContainer;

public class StructurePageWidget extends WidgetGroup {

    public StructurePageWidget(ControllerDefinition controllerDefinition, TabContainer tabContainer) {
        super(20, 0, 176, 256);
        ResourceTexture page = new ResourceTexture("multiblocked:textures/gui/structure_page.png");
        tabContainer.addTab(new TabButton(0, tabContainer.widgets.size() * 10, 20, 20)
                        .setTexture(page.getSubTexture(176 / 256.0, 216 / 256.0, 20 / 256.0, 20 / 256.0),
                                page.getSubTexture(176 / 256.0, 236 / 256.0, 20 / 256.0, 20 / 256.0)),
                this);
        setClientSideWidget(false);
        if (Multiblocked.isClient()) {
            addWidget(PatternWidget.getPatternWidget(controllerDefinition));
        }
    }

}
