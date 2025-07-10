package org.nandayo.farmquest.model.quest;

import org.jetbrains.annotations.NotNull;

public class QuestProperty {


    public final Property[] values() {
        return new Property[]{NO_BLOCK_DROPS};
    }

    /**
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#HARVEST} -> No quest block drops upon breaking.<br>
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#PLANT} -> NO PROCESS<br>
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#DELIVER} -> NO PROCESS<br>
     * No extra process.
     */
    public final Property NO_BLOCK_DROPS = new Property() {

        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public @NotNull String getString() {
            return "no_block_drops";
        }

        @Override
        public @NotNull String getDisplayName() {
            return "No Block Drops";
        }
    };


    /*
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#HARVEST} -> Remove material gained after completing/dropping a quest.<br>
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#PLANT} -> Remove extra materials that are not planted after completing/dropping a quest.<br>
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#DELIVER} -> Remove extra materials that are not delivered after completing/dropping a quest.<br>
     * No extra process.<br>
     * Note: This doesn't work on {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#HARVEST} when {@link #NO_BLOCK_DROPS} is activated.
     */
    //public final Property REMOVE_EXTRA_MATERIALS; // PLANNED TO DO

    /*
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#HARVEST} -> NO PROCESS<br>
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#PLANT} -> Gives one crop at an action.<br>
     * {@link org.nandayo.farmquest.model.quest.Objective.ObjectiveType#DELIVER} -> NO PROCESS<br>
     * No extra process
     */
    //public final Property GIVE_MATERIALS_ONE_BY_ONE; // PLANNED TO DO



    public interface Property {
        boolean isEnabled();
        void setEnabled(boolean enabled);

        @NotNull String getString();
        @NotNull String getDisplayName();
    }
}
