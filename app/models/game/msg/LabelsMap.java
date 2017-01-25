package models.game.msg;

import java.util.Map;

import models.game.GoGroupType;

public class LabelsMap {
	
	private final Map<Integer, GoGroupType> labelsMap;

	public LabelsMap(Map<Integer, GoGroupType> labelsMap) {
		this.labelsMap = labelsMap;
	}

	public Map<Integer, GoGroupType> getLabelsMap() {
		return labelsMap;
	}

}
