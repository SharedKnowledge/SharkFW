package net.sharkfw.knowledgeBase.spatial;

import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Max Oehme (546545)
 */
public class SpatialProbabilityImplTest {
    ISpatialProbability decider;

    @Before
    public void setUp() {
        decider = new SpatialProbabilityImpl();
    }

    @Test
    public void calculateProbability() throws Exception {
        SpatialInformationImpl testData = new SpatialInformationImpl(100, 1000, 100, 1, 1);
        double previous = 1;

        for (int i=1,j=1000;i<1000 && j>0;i++,j++){
            testData.setProfileEntrancePointWeight(i);
            testData.setProfileExitPointWeight(j);
            for (int k=0;k<=10000;k=k+10){
                testData.setSourceToProfileDistance(k);
                double p = decider.calculateProbability(testData);
                Assert.assertTrue("Input: " + k + ", Actual: " + p + ", Previous: " + previous, i<j?p <= previous:p >= previous);
                previous = p;
            }

            testData.setSourceToProfileDistance(100);
            previous = 1;
            for (int k=0;k<=10000;k=k+10){
                testData.setDestinationToProfileDistance(k);
                double p = decider.calculateProbability(testData);
                Assert.assertTrue("Input: " + k + ", Actual: " + p + ", Previous: " + previous, p <= previous);
                previous = p;
            }

            testData.setDestinationToProfileDistance(100);
            previous = 1;
            for (int k=0;k<=100000;k=k+100){
                testData.setEntranceExitInProfileDistance(k);
                double p = decider.calculateProbability(testData);
                Assert.assertTrue("Input: " + k + ", Actual: " + p + ", Previous: " + previous, p <= previous);
            }
        }

        L.d("Probability Distance Test successful!", this);

    }

    class SpatialInformationImpl implements ISpatialInformation {

        private double sourceToProfile;
        private double entrenceToExit;
        private double destinationToProfile;
        private int weightEntrance;
        private int weightExit;

        public SpatialInformationImpl(double sourceToProfile, double entrenceToExit, double destinationToProfile, int weightEntrance, int weightExit) {
            this.sourceToProfile = sourceToProfile;
            this.entrenceToExit = entrenceToExit;
            this.destinationToProfile = destinationToProfile;
            this.weightEntrance = weightEntrance;
            this.weightExit = weightExit;
        }

        @Override
        public double getSourceToProfileDistance() {
            return this.sourceToProfile;
        }

        @Override
        public double getEntranceExitInProfileDistance() {
            return this.entrenceToExit;
        }

        @Override
        public double getDestinationToProfileDistance() {
            return this.destinationToProfile;
        }

        @Override
        public int getProfileEntrancePointWeight() {
            return this.weightEntrance;
        }

        @Override
        public int getProfileExitPointWeight() {
            return this.weightExit;
        }

        public void setSourceToProfileDistance(double sourceToProfile) {
            this.sourceToProfile = sourceToProfile;
        }

        public void setEntranceExitInProfileDistance(double entrenceToExit) {
            this.entrenceToExit = entrenceToExit;
        }

        public void setDestinationToProfileDistance(double destinationToProfile) {
            this.destinationToProfile = destinationToProfile;
        }

        public void setProfileEntrancePointWeight(int weightEntrance) {
            this.weightEntrance = weightEntrance;
        }

        public void setProfileExitPointWeight(int weightExit) {
            this.weightExit = weightExit;
        }
    }

}