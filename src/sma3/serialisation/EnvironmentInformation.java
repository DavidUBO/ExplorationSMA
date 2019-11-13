package sma3.serialisation;

import java.util.ArrayList;
import java.util.List;

import exploration.Direction;
import sma.common.DirectionUtil;

public class EnvironmentInformation {

	public List<CaseLightModel> casesAlentour;
	public Direction direction;
	
	public EnvironmentInformation(String serializedInfo) {
		String[] infos = serializedInfo.split("_");
		String[] cases = infos[0].split(";");
		
		casesAlentour = new ArrayList<CaseLightModel>();
		for (String infoCase : cases)
			casesAlentour.add(new CaseLightModel(infoCase));
		
		if (infos.length >= 2)
			direction = DirectionUtil.stringToDirection(infos[1]);
		else
			direction = null;
	}
}
