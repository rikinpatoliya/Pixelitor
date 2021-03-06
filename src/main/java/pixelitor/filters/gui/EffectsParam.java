/*
 * Copyright 2018 Laszlo Balazs-Csiki and Contributors
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.filters.gui;

import pixelitor.filters.painters.AreaEffects;
import pixelitor.filters.painters.EffectsPanel;
import pixelitor.gui.utils.DialogBuilder;

import javax.swing.*;
import java.awt.Rectangle;

import static javax.swing.BorderFactory.createTitledBorder;
import static pixelitor.filters.gui.RandomizePolicy.IGNORE_RANDOMIZE;

/**
 * A {@link FilterParam} for shape effects in a dialog
 */
public class EffectsParam extends AbstractFilterParam {
    private EffectsPanel effectsPanel;
    private final boolean separateDialog;

    public EffectsParam(String name) {
        super(name, IGNORE_RANDOMIZE); // randomize() is not implemented!
        this.separateDialog = true;
    }

    @Override
    public JComponent createGUI() {
        assert adjustmentListener != null;
        effectsPanel = new EffectsPanel(adjustmentListener, null);

        if (separateDialog) {
            DefaultButton button = new DefaultButton(effectsPanel);
            effectsPanel.setDefaultButton(button);

            ConfigureParamGUI configureParamGUI = new ConfigureParamGUI(owner ->
                    buildDialog(owner, true), button);

            paramGUI = configureParamGUI;
            setParamGUIEnabledState();
            return configureParamGUI;
        } else {
            effectsPanel.setBorder(createTitledBorder("Effects"));
            return effectsPanel;
        }
    }

    public JDialog buildDialog(JDialog owner, boolean modal) {
        DialogBuilder db = new DialogBuilder();
        if (owner != null) {
            db = db.owner(owner);
        }
        if (!modal) {
            db = db.notModal();
        }
        return db
                .title("Effects")
                .content(effectsPanel)
                .withScrollbars()
                .okText("Close")
                .noCancelButton()
                .build();
    }

    public AreaEffects getEffects() {
        // if a GUI filter is running without a GUI
        // (for example in a RandomGUITest), the panel needs to be created here
        if (effectsPanel == null) {
            effectsPanel = new EffectsPanel(adjustmentListener, null);
        }

        return effectsPanel.getEffects();
    }

    public void setEffects(AreaEffects effects) {
        if (effectsPanel == null) { // probably never true
            effectsPanel = new EffectsPanel(adjustmentListener, effects);
            return;
        }

        effectsPanel.setEffects(effects);
    }

    @Override
    public void randomize() {
        // TODO
    }

    @Override
    public void considerImageSize(Rectangle bounds) {
        // ignore
    }

    @Override
    public ParamState copyState() {
        return getEffects();
    }

    @Override
    public void setState(ParamState state) {
        setEffects((AreaEffects) state);
    }

    @Override
    public boolean canBeAnimated() {
        return false;
    }

    @Override
    public int getNumGridBagCols() {
        return separateDialog ? 2 : 1;
    }

    @Override
    public boolean isSetToDefault() {
        if (effectsPanel != null) {
            return effectsPanel.isSetToDefault();
        }
        return true;
    }

    @Override
    public void reset(boolean trigger) {
        if (effectsPanel != null) {
            effectsPanel.reset(trigger);
        }
    }
}
