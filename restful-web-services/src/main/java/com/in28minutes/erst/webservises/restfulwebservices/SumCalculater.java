package com.in28minutes.erst.webservises.restfulwebservices;

import java.util.ArrayList;

public class SumCalculater {
	public static SumCalculater instance = null;
	private int strikeValue = 10;
	private SumCalculater() {}

	public static SumCalculater getInstance() {
		if(instance == null)
			instance = new SumCalculater();   	 
		return instance;
	}     

	public String getCalculateSum(ArrayList<Long[]> list) {
		int[] points1 = new int[list.size()];
		int[] points2 = new int[list.size()];

		for(int i= 0; i< list.size(); i++) {
			Long[] lList = list.get(i);
			for(int k= 0; k< lList.length; k++){
				long value = lList[k];
				if(k == 0) {
					points1[i] = (int)value;
				}else {
					points2[i] = (int)value;
				}
			}
		}		
		int[] mySum = updateSum(points1, points2);		
		return createSumString(mySum);	 
	}

	private String createSumString(int[] mySum) {
		String newsum = "";    	
		for(int i= 0; i< mySum.length; i++){
			String end = i+1 < mySum.length ? "," : "";
			newsum = newsum +  mySum[i] + end;
		}
		return  "[" + newsum + "]";
	}

	private int[] updateSum(int[] points1, int[] points2) {
		int size = points1.length > 10 ?  10 : points1.length;
		int[] mySum = new int[size];
		if(isAllStrikes(points1)) {
			return calculateSumForAllStrikes(points1, points2, mySum);
		}else {		
			for(int i= 0; i< mySum.length; i++) {
				int pointSum = points1[i] + points2[i];
				if(i == 0) {
					mySum[i] = pointSum;
					continue;
				}
				boolean isStrikeBefore = isStrike(points1[i-1]);
				boolean isSpareBefore = isSpare(points1[i-1],  points2[i-1])? true : false;
				boolean isStrikeMoreBefore = i>1 ? isStrike(points1[i-2]) : false;

				if(points1.length == 11 && i == 9) {
					pointSum = points1[9] + points2[9] + points1[10] + points2[10];
					if(isStrikeBefore) {
						if(points1[9] != strikeValue) {
					     mySum[i-1] = mySum[i-1] + points1[9] + points2[9];
						}else {
						 mySum[i-1] = mySum[i-1] + points1[9] + points2[10];
						}
					}else if(isSpareBefore) {
						mySum[i-1] = mySum[i-1] + points1[i];
					}
					mySum[9] = mySum[8] + pointSum;
					break;
				}else {
					if(isStrikeMoreBefore) {		
						if(isStrikeBefore) {
							mySum[i-2] = mySum[i-2] + points1[i];
							mySum[i-1] = mySum[i-1] + points1[i]+ pointSum; 
						}else if(isSpareBefore) {
							mySum[i-1] = mySum[i-1] + points1[i];
						}
						mySum[i] = mySum[i-1] + pointSum;
					}else if(isSpareBefore) {
						mySum[i-1] = mySum[i-1] + points1[i];
						mySum[i] = mySum[i-1] + pointSum; 
					}else if(isStrikeBefore) {
						mySum[i-1] = mySum[i-1] + pointSum;
						mySum[i] = mySum[i-1] + pointSum; 
					}else {					
						mySum[i] = mySum[i-1] + pointSum;
					}
				}
			}
			return mySum;
		}
	}

	private int[] calculateSumForAllStrikes(int[] points1, int[] points2, int[] mySum) {
		for(int i= 0; i< points1.length; i++) {		
			int pointSum = points1[i] + points2[i];
			if(i == 0) {
				mySum[i] = pointSum;
				continue;
			}
			if(i == 1) {
				mySum[0] = mySum[0] + pointSum;
				mySum[i] = mySum[0] + pointSum;
			}else if(i > 9) {			
				mySum[8] = mySum[8] + points1[i];
				mySum[9] = mySum[8] + pointSum + points2[i];	
			}else {
				mySum[i-2] = mySum[i-2] + pointSum;
				mySum[i-1] = mySum[i-2] + pointSum + pointSum;
				mySum[i] = mySum[i-1] + pointSum;
			}
		}
		return mySum;
	}

	private boolean isAllStrikes(int[] points1) {
		boolean isStrike = true;
		for(int i : points1) {
			if(i != strikeValue) {
				isStrike = false;
				break;
			}
		}
		return isStrike;
	}


	private boolean isSpare(int point1, int point2) {
		if(point1 == strikeValue) return false;
		if(point2 == strikeValue) return true;
		return (point1 + point2) == strikeValue ;
	}

	private boolean isStrike(int point) {
		return point == strikeValue;
	}

}
