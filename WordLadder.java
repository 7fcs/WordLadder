// Name: Chloe Pham
// References:
// lines: 86, 104 - https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html (HashMap methods like getOrDefault())

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordLadder {

    public static List<List<String>> solve(File file, String start, String goal, boolean pt2) {             // method will return list of solutions at the end
        Set<String> words = new HashSet<>();                                                                // this will store all possible words
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {                                                                 // while the file has another line (another word)
                String word = scanner.nextLine();                                                           // take that word
                words.add(word);                                                                            // add it to the list of all possible words
            }
        } catch (FileNotFoundException e) {                                                                 // if the file isn't found it throws exception
            System.out.println("File not found");
            return null;
        }

        List<List<String>> sols = new ArrayList<>();                                                        // this holds all solution paths from start to goal word
        if (!words.contains(goal)) {                                                                        // if the goal isn't possible then method returns null
            return null;
        }

        Map<String, List<String>> buckets = buildBuckets(words);                                            // this holds all the buckets like "pope" is in bucket "_ope" and stuff
        Queue<List<String>> queue = new LinkedList<>();                                                     // queue holds paths to goal

        List<String> initial = new ArrayList<>();                                                           // what we initially start with is the first path                                             // 
        initial.add(start);                                                                                 // which only starts with the starting word
        queue.add(initial);                                                                                 // path is added to queue to be explored for next words connected to that word


        Set<String> seen = new HashSet<>();                                                                 // keep track of seen words so we don't go in circles
        boolean foundGoal = false;                                                                          // false when we haven't found the goal word yet

        while (!queue.isEmpty() && !foundGoal) {                                                            // while there's paths to go thru and we haven't found the goal
            int stepSize = queue.size();                                                                    // how many adjacent words there are (this step)
            Set<String> seenStep = new HashSet<>();                                                         // seen words specifically on this step

            for (int i = 0; i < stepSize; i++) {                                                            // go through each path in this step
                List<String> path = queue.poll();                                                           // get path from queue
                String lastWord = path.get(path.size() - 1);                                                // get last word from current path


                List<String> nextWords;                                                                     // will hold next valid steps from the last word
                if (pt2) {                                                                                  // if this is pt2 of the hw
                    nextWords = partTwo(lastWord, buckets);                                                 // use replace,insert,delete
                }
                else {                                                                                      // if its just pt1 of hw
                    nextWords = partOne(lastWord, buckets);                                                 // we only use replacement of chars
                }

                for (String nextWord : nextWords) {                                                         // go thru all connected words
                    if (!seen.contains(nextWord)) {                                                         // skip words seen before

                        List<String> newPath = new ArrayList<>(path);                                       // makes a copy of the path
                        newPath.add(nextWord);                                                              // adds the next valid word to that path

                        if (nextWord.equals(goal)) {                                                        // if that is the goal word
                            sols.add(newPath);                                                              // save this successful path
                            foundGoal = true;                                                               // stop after finishing this step (we only care abt min steps)
                        }
                        else {
                            queue.add(newPath);                                                             // if not then we just keep looking
                            seenStep.add(nextWord);                                                         // mark this word as seen at this step
                        }
                    }
                }
            }

            seen.addAll(seenStep);                                                                          // update overall seen list
        }

        return sols;                                                                                        // return all the solution paths found
    }

    private static List<String> partOne(String word, Map<String, List<String>> buckets) {                   // pt1 of hw is just replacing char
        List<String> nextWords = new ArrayList<>();                                                         // will hold next valid words from last word

        for (int i = 0; i < word.length(); i++) {                                                           
            String bucket = word.substring(0, i) + "_" + word.substring(i + 1);                             // replace ith char with _ for the key
            List<String> inTheBucket = buckets.getOrDefault(bucket, new ArrayList<>());                     // find all words maybeNext in this bucket (need getOrDefault to avoid null pointer exception)

            for (String maybeNext : inTheBucket) {                                                          // for all the maybeNext words in the bucket
                if (!maybeNext.equals(word)) {                                                              // if the word doesn't equal the last word
                    nextWords.add(maybeNext);                                                               // it will be added to valid next word
                }
            }
        }

        return nextWords;                                                                                   // returns the list of next valid words
    }

    private static List<String> partTwo(String word, Map<String, List<String>> buckets) {                   // valid words for part 2 of hw
        Set<String> nextWords = new HashSet<>(partOne(word, buckets));                                      // valid words contains results from part 1


        for (int i = 0; i <= word.length(); i++) {                                                          // this one is for insertion & helps with deletion
            String bucket = word.substring(0, i) + "_" + word.substring(i);                                 // the bucket we are looking at just has the word with an _ inserted somewhere in the word
            List<String> inTheBucket = buckets.getOrDefault(bucket, new ArrayList<>());                     // get the list of words that are in the bucket 
            for (String maybeNext : inTheBucket) {                                                          // for all the words that might be a next word in that bucket 
                if (!maybeNext.equals(word)) {                                                              // as long as that maybe word isn't the word we're trying to connect with
                    nextWords.add(maybeNext);                                                               // add that word to list of valid next words
                }
            }
        }

        return new ArrayList<>(nextWords);                                                                  // returns the valid words

        

    }

    private static Map<String, List<String>> buildBuckets(Set<String> words) {
        Map<String, List<String>> buckets = new HashMap<>();                                                // map of all the possible buckets

        for (String word : words) {                                                                         // for all the words
            int len = word.length();                                                                        // length for word to limit for loop      

            for (int i = 0; i < len; i++) {                                                                 // this covers replacement and deletion (stop when i=0 is _top)
                String bucket = word.substring(0, i) + "_" + word.substring(i + 1);                         // bucket for underscore replacementplaced
                if (!buckets.containsKey(bucket)) {
                    buckets.put(bucket, new ArrayList<>());
                }
                buckets.get(bucket).add(word);
                
            }

            for (int i = 0; i <= len; i++) {                                                                // this covers insertion and deletion (top when i = 0 is _top therefore stop and top are connected)
                String bucket = word.substring(0, i) + "_" + word.substring(i);                             // bucket for inserted underscore placed
                if (!buckets.containsKey(bucket)) {                                                         // if bucket does not exist
                    buckets.put(bucket, new ArrayList<>());                                                 // create new bucket with empty list
                }
                buckets.get(bucket).add(word);                                                              // add the word to the bucket
                
            }
        }

        return buckets;                                                                                     // return all the map of buckets

    }

    public static void printResults(String start, String end, List<List<String>> sols) {                    // just a normal print method
        System.out.println(start + " --> " + end);

        if (sols == null || sols.isEmpty()) {
            System.out.println("There is no solution in this case");
            return;
        }

        int steps = sols.get(0).size() - 1;                                                                 // the size -1 of one of the paths is how many steps it takes
        System.out.println("the minimum number of the steps to solve the puzzle is " + steps + ".");        // print min number of steps
        System.out.println("the number of solutions with the minimal steps is " + sols.size());             // print how many solutions there are with that many of steps

        for (List<String> sol : sols) {                                                                     // for each solution/path
            String result = "["; 
        
            for (int i = 0; i < sol.size(); i++) {
                result += sol.get(i) + " (" + i + ")";                                                      // gets the word and the step number
                if (i != sol.size() - 1) {                                                                  // adds commas after the step numbers except at the very end
                    result += ", ";
                }
            }
        
            result += "]";
            System.out.println(result);                                                                     // print out the path with words and steps and then go back to print rest of the paths
        }

        System.out.println();                                                                               // just easier to read with extra line
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {                                                                             // must take 3 arguments (file, start word, goal word)
            System.out.println("Need 3 arguments");
            return;
        }

        String fileName = args[0];                                                                          // pretty self explanatory
        String start = args[1];
        String goal = args[2];

        File file = new File(fileName);
        boolean pt2 = false;
        if (fileName.equals("words_alpha.txt")) {                                                           // this detects whether or not we need to use pt2 rules
            pt2 = true;
        }

        List<List<String>> sols = solve(file, start, goal, pt2);
        printResults(start, goal, sols);
    }
}
