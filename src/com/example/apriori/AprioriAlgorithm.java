package com.example.apriori;

import java.util.*;

public class AprioriAlgorithm {
    
    public static void main(String[] args) {
        // The database of transactions
        List<List<String>> transactions = new ArrayList<>();
        transactions.add(Arrays.asList("bread", "milk"));
        transactions.add(Arrays.asList("bread", "diapers", "beer", "eggs"));
        transactions.add(Arrays.asList("milk", "diapers", "beer", "cola"));
        transactions.add(Arrays.asList("bread", "milk", "diapers", "beer"));
        transactions.add(Arrays.asList("bread", "milk", "diapers", "cola"));
        
        // Minimum support count threshold
        int minSupportCount = 3;
        
        // Generate frequent itemsets
        List<List<String>> frequentItemsets = generateFrequentItemsets(transactions, minSupportCount);
        
        // Print the frequent itemsets
        System.out.println("Frequent Itemsets:");
        for (List<String> itemset : frequentItemsets) {
            System.out.println(itemset);
        }
    }
    
    public static List<List<String>> generateFrequentItemsets(List<List<String>> transactions, int minSupportCount) {
        // Step 1: Generate candidate 1-itemsets
        List<List<String>> candidateItemsets = generateCandidateItemsets(transactions);

        // Step 2: Prune candidate 1-itemsets
        List<List<String>> frequentItemsets = pruneItemsets(transactions, candidateItemsets, minSupportCount);

        // Step 3: Generate larger k-itemsets until no more frequent itemsets can be found
        int k = 3;
        while (!frequentItemsets.isEmpty()) {
            List<List<String>> candidateKItemsets = generateCandidateKItemsets(frequentItemsets, k);
            frequentItemsets = pruneItemsets(transactions, candidateKItemsets, minSupportCount);
            k++;
        }

        // Prune the last set of candidate itemsets
        frequentItemsets = pruneItemsets(transactions, candidateItemsets, minSupportCount);

        return frequentItemsets;
    }

    
    public static List<List<String>> generateCandidateItemsets(List<List<String>> transactions) {
        List<List<String>> candidateItemsets = new ArrayList<>();
        
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                List<String> itemset = new ArrayList<>();
                itemset.add(item);
                if (!candidateItemsets.contains(itemset)) {
                    candidateItemsets.add(itemset);
                }
            }
        }
        
        return candidateItemsets;
    }
    
    public static List<List<String>> generateCandidateKItemsets(List<List<String>> transactions, int k) {
        List<List<String>> candidateItemsets = new ArrayList<>();

        int n = transactions.size();
        for (int i = 0; i < n - 1; i++) {
            List<String> itemset1 = transactions.get(i);
            for (int j = i + 1; j < n; j++) {
                List<String> itemset2 = transactions.get(j);

                // Check if itemsets are joinable
                boolean joinable = true;
                for (int l = 0; l < k - 1; l++) {
                    if (!itemset1.get(l).equals(itemset2.get(l))) {
                        joinable = false;
                        break;
                    }
                }

                if (joinable) {
                    // Join the itemsets
                    List<String> joinedItemset = new ArrayList<>(itemset1);
                    joinedItemset.add(itemset2.get(k - 1));

                    // Check if the joined itemset contains subsets that are not frequent
                    boolean hasInfrequentSubset = false;
                    for (int m = 0; m < k - 1; m++) {
                        List<String> subset = new ArrayList<>(joinedItemset);
                        subset.remove(m);
                        if (!candidateItemsets.contains(subset)) {
                            hasInfrequentSubset = true;
                            break;
                        }
                    }

                    if (!hasInfrequentSubset) {
                        candidateItemsets.add(joinedItemset);
                    }
                }
            }
        }

        return candidateItemsets;
    }

    
    public static List<List<String>> pruneItemsets(List<List<String>> transactions, List<List<String>> candidateItemsets, int minSupportCount) {
        List<List<String>> frequentItemsets = new ArrayList<>();
        
        Map<List<String>, Integer> itemsetCounts = new HashMap<>();
        
        // Count the support for each candidate itemset
        for (List<String> transaction : transactions) {
            for (List<String> candidateItemset : candidateItemsets) {
                if (transaction.containsAll(candidateItemset)) {
                    itemsetCounts.put(candidateItemset, itemsetCounts.getOrDefault(candidateItemset, 0) + 1);
                }
            }
        }
        
        // Select frequent itemsets based on the minimum support count
        for (Map.Entry<List<String>, Integer> entry : itemsetCounts.entrySet()) {
            if (entry.getValue() >= minSupportCount) {
                frequentItemsets.add(entry.getKey());
            }
        }
        
        return frequentItemsets;
    }
}

