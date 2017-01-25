package models.game.msg;

import java.util.List;

public class LockedGroups {
	
	private final List<Integer> lockedGroups;

	public LockedGroups(List<Integer> lockedGroups) {
		this.lockedGroups = lockedGroups;
	}

	public List<Integer> getLockedGroups() {
		return lockedGroups;
	}

}
