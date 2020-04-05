package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) throws IOException {
    File sampleFile = new File("./src/datafolder/sampleFile.csv");
    BufferedReader reader;

    if (!sampleFile.exists()) {
      //The file doesn't exist, so we have to exit
      System.out.println("The file doesn't exist.");
      System.exit(1);
    }

    //File reading section
    reader = new BufferedReader(new FileReader(sampleFile));
    String line = null;
    int transactionTotal = 0;

    //Read first line and split at headers
    String[] features = reader.readLine().split(",");
    int featureNum = features.length;

    List<int[]> sampleResults = new ArrayList<>();

    while ((line = reader.readLine()) != null) {
      //Split each line in the array, and add the array to sampleResults
      sampleResults.add(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray());
      transactionTotal++;
    }
    reader.close();

    //Affinity Analysis begins here

    Map<String, Integer> fullResults = new HashMap<>();
    Map<HashSet<String>, Integer> validResults = new HashMap<>();

    for (int[] sample : sampleResults) {
      //Loop through the array's ints
      for (int premise = 0; premise < featureNum; premise++) {
        if (sample[premise] == 1) {
          fullResults.put(features[premise], fullResults.getOrDefault(features[premise], 0) + 1);
        }
        for (int conclusion = 0; conclusion < featureNum; conclusion++) {
          //If premise and the conclusion are the same, skip it.
          if (conclusion == premise) {
            continue;
          }
          //Also check if the conclusion is also 1.
          if (sample[conclusion] == 1) {
            validResults
                .put(new HashSet<String>(Arrays.asList(features[premise], features[conclusion])),
                    validResults.getOrDefault(
                        new HashSet<String>(Arrays.asList(features[premise], features[conclusion])),
                        0) + 1);
          }
        }
      }
    }
    //Calculate and print Confidences and Support
    for (HashSet<String> setOfFeatures : validResults.keySet()) {
      List<String> listOfFeatures = setOfFeatures.stream().collect(Collectors.toList());

      //First find how many times there's 2+ items together for Confidences
      //And then how many times an item was there at all for Support
      double confidence =
          (double) fullResults.get(listOfFeatures.get(0)) / validResults.get(setOfFeatures);
      double support = (double) validResults.get(setOfFeatures) / transactionTotal;

      //Print Confidence and feature sets
      System.out.printf("We show a confidence of %f that a person that "
              + "purchased %s will also buy %s%n       and a support of %f that "
              + "a person will purchase these items together at all.%n", confidence, listOfFeatures.get(0),
          listOfFeatures.get(1), support);
    }
  }
}
