/*
Copyright 2017 Le Duyen Sandra Vu

This file is part of ForestForGe.
ForestForGe is free software: you can redistribute it and/or modify it under the terms of 
the GNU General Public License as published by the Free Software Foundation, either 
version 3 of the License, or (at your option) any later version.
ForestForGe is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with ForestForGe.  
If not, see <http://www.gnu.org/licenses/>.
*/

/*		
		HashMap<List<String>,Map<String,Integer>> featCounts = new HashMap<List<String>,Map<String,Integer>>();

				// If the Feature is not already in the Map
				if(featCounts.get(feature) == null){
						LinkedHashMap<String, Integer> tempMap	= new LinkedHashMap<String,Integer>();
						tempMap.put(label, 1);
						featCounts.put(feature, tempMap);
				}
				else{
					// If the Feature is already in the map, but the label is not
					if(featCounts.get(feature).get(label) == null){
						featCounts.get(feature).put(label, 1);
					}
					else{
						// Increment
						int count = featCounts.get(feature).get(label);
						featCounts.get(feature).put(label, count++);
					}
				}
		
		
		
		
		
		int[][] freqTable;

		// Construct freqTable
			// Get the ID of the feature and label, than add the frequency to the matrix
			double sum = 0.0;
			freqTable = new int[vocab.size()][labelMap.size()];
			for(List<String> feat : vocab.keySet()){
				int vID = vocab.get(feat);
				for(String lab : labelMap.keySet()){
					int lID = labelMap.get(lab);
					if(featCounts.get(feat).get(lab) != null){
						freqTable[vID][lID] = featCounts.get(feat).get(lab);
						sum = sum + freqTable[vID][lID]*freqTable[vID][lID];
					}
					else{
						freqTable[vID][lID] = 0;
					}
				}
			}
		
		
		
		
		
		
		
			double[][] normalizedFreqTable;

		
			double lengthOfVec = Math.sqrt(sum);
			
			normalizedFreqTable = new double[vocab.size()][labelMap.size()];
			for(int i = 0; i<vocab.size(); i++){
    			for(int j = 0; j<labelMap.size(); j++){
        			normalizedFreqTable[i][j] = freqTable[i][j] / lengthOfVec;
    			}
			}
		
		// Feature Selection
		FeatureSelect fs 	= new FeatureSelect(20, "Frequency");
		LinkedHashMap<Integer, Double> valvocab = new LinkedHashMap<Integer,Double>();

		valvocab 	= fs.select(vocab, normalizedFreqTable);
*/