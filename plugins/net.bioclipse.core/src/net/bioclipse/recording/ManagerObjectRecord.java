package net.bioclipse.recording;

import net.bioclipse.recording.MethodRecord.BioObjectParamater;
import net.bioclipse.recording.MethodRecord.NonBioObjectParamater;
import net.bioclipse.recording.MethodRecord.Paramater;

public class ManagerObjectRecord extends MethodRecord {

	protected String managerObjectName;
	
	public ManagerObjectRecord( String methodName, 
			                    String managerObjectName,
			                    Object[] parameters, 
			                    Object returnValue ) {
		
		super(methodName, parameters, returnValue);
		this.managerObjectName = managerObjectName;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < paramaters.size(); i++) {
			Paramater p = paramaters.get(i);
			
			if( p instanceof NonBioObjectParamater ) {
				sb.append( ( (NonBioObjectParamater)p ).stringRepresentation );
			}
			else if( p instanceof BioObjectParamater) {
				sb.append(p.type);
			}
			else {
				throw new IllegalStateException( "Unrecognized " +
						                         "paramater type: " + p ); 
			}
			
			if(i != paramaters.size() - 1) {
				sb.append(", ");
			}
			else {
				sb.append(' ');
			}
		}
		
		return managerObjectName + "." + methodName + "( "
			+ sb.toString() + ")";
	}

	public String getManagerObjectName() {
		return managerObjectName;
	}

	public void setManagerObjectName(String managerObjectName) {
		this.managerObjectName = managerObjectName;
	}

}
