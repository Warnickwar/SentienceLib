package org.warnickwar.mindlib.base;

import org.warnickwar.mindlib.base.identifier.AiIdentifier;
import org.warnickwar.mindlib.base.identifier.IdentifiedData;

import java.util.*;

public class ActionTree {

    private static final HashSet<IdentifiedData<Belief>> DEFAULT_BELIEFS = new HashSet<>();
    private static final AiIdentifier<Twig> ROOT_ID = new AiIdentifier<>("root");

    private final IdentifiedData<Twig> root;

    public ActionTree() {
        this.root = IdentifiedData.of(ROOT_ID, new Twig(DEFAULT_BELIEFS));
    }

    public IdentifiedData<Twig> getRoot() {
        return root;
    }

    public abstract static class Branch {

        Branch() {}

        abstract HashSet<IdentifiedData<Belief>> preconditions();
    }

    public static class Twig extends Branch {

        private final HashSet<IdentifiedData<Belief>> preconditions;
        private final HashSet<IdentifiedData<Branch>> branches = new HashSet<>();

        Twig(HashSet<IdentifiedData<Belief>> preconditions) {
            this.preconditions = preconditions;
        }

        public boolean hasId(AiIdentifier<Branch> id) {
            for (IdentifiedData<Branch> branch : branches) {
                if (branch.isId(id)) return true;
            }
            return false;
        }

        public Twig createNewChild(AiIdentifier<Branch> id, HashSet<IdentifiedData<Belief>> preconditions) {
            Twig res =  new Twig(preconditions);
            branches.add(IdentifiedData.of(id, res));
            return res;
        }

        public Twig addNewAction(AiIdentifier<Action> id, Action action) {
            branches.add(IdentifiedData.of(id.castTo(), new Leaf(action)));
            return this;
        }

        public Twig removeAction(AiIdentifier<Action> id) {
            AiIdentifier<Branch> castedName =  id.castTo();
            branches.removeIf(branch -> branch.isId(castedName) && branch.getValue() instanceof Leaf);
            return this;
        }

        public Twig cullBranch(AiIdentifier<Branch> id) {
            branches.removeIf(branch -> branch.isId(id) && !(branch.getValue() instanceof Leaf));
            return this;
        }

        HashSet<IdentifiedData<Belief>> preconditions() {
            return new HashSet<>(preconditions);
        }

        HashSet<IdentifiedData<Branch>> branches() {
            return new HashSet<>(branches);
        }
    }

    private static class Leaf extends Branch {

        private final Action action;

        Leaf(Action action) {
            this.action = action;
        }

        @Override
        HashSet<IdentifiedData<Belief>> preconditions() {
            return action.getPreconditions();
        }

        Action getAction() {
            return action;
        }

    }
}
