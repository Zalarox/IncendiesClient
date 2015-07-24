package src;

public final class OnDemandRequest extends NodeSub {

	public OnDemandRequest() {
		incomplete = true;
	}

	int dataType;
	byte buffer[];
	int id;
	boolean incomplete;
	int loopCycle;

	@Override
	public String toString() {
		return "OnDemandData [dataType=" + dataType + ", ID=" + id
				+ ", incomplete=" + incomplete + ", loopCycle=" + loopCycle
				+ "]";
	}

}
