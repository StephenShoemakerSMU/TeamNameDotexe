import java.util.ArrayList;

public class IRArrayDensity {
    private ArrayList<Character> beacons;
    private ArrayList<Integer> density;

    public IRArrayDensity(char[][] matrix){
        beacons = new ArrayList<Character>();
        density = new ArrayList<Integer>();
        for(int rowIndex = 0; rowIndex < matrix.length; rowIndex++){
            ArrayList<Character> charactersPresent = new ArrayList<Character>();
            for(int colIndex = 0; colIndex < matrix[rowIndex].length; colIndex++ ){
                if(!charactersPresent.contains(new Character(matrix[rowIndex][colIndex]))){
                    charactersPresent.add(new Character(matrix[rowIndex][colIndex]));
                }
            }

            int densityValues[] = new int[charactersPresent.size()];
            for(int characterIndex = 0; characterIndex < charactersPresent.size(); characterIndex++){

                for(int matrixIndex = 0; matrixIndex < matrix[rowIndex].length; matrixIndex++){
                    if(new Character(matrix[rowIndex][matrixIndex]) == charactersPresent.get(characterIndex)){
                        densityValues[characterIndex]++;
                    }
                }
            }

            int highestDensity = 0;
            for(int index = 0; index < densityValues.length; index++){
                if(densityValues[index] > densityValues[highestDensity]){
                    highestDensity = index;
                }
            }
            //System.out.println("HERE");
            //System.out.println(charactersPresent.get(highestDensity));
            beacons.add(charactersPresent.get(highestDensity));
            density.add(densityValues[highestDensity]);
        }
    }

    public ArrayList<Character> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<Character> beacons) {
        this.beacons = beacons;
    }

    public ArrayList<Integer> getDensity() {
        return density;
    }

    public void setDensity(ArrayList<Integer> density) {
        this.density = density;
    }

    public int indexBetween(char beaconOne, char beaconTwo){
        //we are finding where the specific beacon has the highest denstiy
        int beaconOneIndex = -1;
        for(int index = 0; index < beacons.size(); index++){
            if(beaconOneIndex == -1 && beacons.get(index) == beaconOne){
                beaconOneIndex = index;
            }
            else if(beacons.get(index) == beaconOne && density.get(index) > density.get(beaconOneIndex)){
                beaconOneIndex = index;
            }
        }

        int beaconTwoIndex = -1;
        for(int index = 0; index < beacons.size(); index++){
            if(beaconTwoIndex == -1 && beacons.get(index) == beaconTwo){
                beaconTwoIndex = index;
            }
            else if(beacons.get(index) == beaconTwo && density.get(index) > density.get(beaconTwoIndex)){
                beaconTwoIndex = index;
            }
        }

        return Math.abs(beaconTwoIndex - beaconOneIndex);

    }

    public void display(){
        for(int index = 0; index < beacons.size(); index++){
            System.out.println("Angle " + index*5 + ": " + beacons.get(index) + "Density: " + density.get(index));
        }
    }
}
