import java.io.*;
import java.util.*;

public class Lab4
{
    /**
     *  Problem: Sort multi-digit integers (with n total digits) in O(n) time.
     *  (Technically, it is O(n * b) time. However, since our base b = 128 is constant, it is O(n).)
     */
     /*my code V*/
   private static void problem(byte[][] arr)
   {  
      //int smlCnt = arr[1].length;
      int lrgCnt = 0;
      //gets the smallest and largest length of an array in the set of arrays
      for(byte[] i : arr)//O(i)
      {
         lrgCnt = i.length > lrgCnt? i.length : lrgCnt;
         //smlCnt = i.length < smlCnt? i.length : smlCnt;
      }
      //creats an array, lengthSrt, which has the number of elements of each array in arr.
      /*ex: The double array :
            1,2,4
            1,2
            3
            3,1,3
            1,3
            
            would produce :
       index:0  1  2  3
            {0, 1, 2, 2} as the indecies of the array*/
        
      int[] lengthSrt = new int[lrgCnt];
      for(int i = 0; i < arr.length; i++)//O(i)
      {
         lengthSrt[arr[i].length - 1] ++;
      }
      //add the indecies of lengthSrt together 
      //ex from above would be: 0,1,3,5
      for(int i = 1; i < lengthSrt.length; i++)//O(>i)
      {
         lengthSrt[i] += lengthSrt[i - 1];
      }
      //sorts the array lengths into a temporary double array. Can be viewed as seperate blocks
      /* ex from above would be:
         3
         ----------block 1
         1,2
         1,3
         ----------block 2
         1,2,4
         3,1,3
         ----------block 3
         */
      byte[][] tempArr = new byte [arr.length][0];
      for(int i = arr.length - 1; i >= 0; i--) //O(i)
      {
         tempArr[lengthSrt[arr[i].length - 1] - 1] = arr[i];
         lengthSrt[arr[i].length - 1]--;
      }
      //puts the newly paritally sorted array into the original array. Also, creates another double array of how many of one length array there is
      //in a row, and what that length is:
      /* ex from above:
         [1,1]
         [2,2]
         [2,3]
         this way, you will not have to iterate more times than necessary when fully sorting the array. */
      int arrLengthsCnt = 1;
      byte[] foo = tempArr[0];
      for(byte[] i : tempArr)
      {
         if(i.length != foo.length)
         {
            arrLengthsCnt++;
            foo = i;
         }
      }
      int[][] arrLengths = new int [arrLengthsCnt][2];
      arrLengthsCnt = 0;
      int numOfColumns = 0;//to be used later in initalizing the number of rows in elementCount array
      for(int i = 0; i < arr.length; i++) //O(i)
      {
         arrLengths[arrLengthsCnt][0] += 1;
         if(i < arr.length - 1 && tempArr[i + 1].length != tempArr[i].length)
         {
               arrLengths[arrLengthsCnt][1] = tempArr[i].length;
               if(arrLengthsCnt != arrLengths.length - 1)
               {
                  arrLengthsCnt++;
               }
         }       
         else 
         {
            arrLengths[arrLengthsCnt][1] = tempArr[tempArr.length - 1].length;
         }
         arr[i] = tempArr[i];
      }
      
      //used later when initializing elementCount
      for(int[] i : arrLengths)
      {
         numOfColumns += i[1];
      }
      //finds the number of times each index (up to 128) of elementCount[i] appears in the columns of each block of arr 
      //(the blocks and blocks column count will be indentified using arrLengths[block][1])
      /* ex:
         3
         ----------block 1
         1,2
         1,3
         ----------block 2
         1,2,4
         3,1,3
         ----------block 3
         
         would produce:
         
         index:   0,1,2,3,4...128
         
elementCount[0]   0 0 0 1 0...0
               -----------------block1
elementCount[1]   0 2 0 0 0...0
elementCount[2]   0 0 1 1 0...0 
               -----------------block2
elementCount[3]   0 1 0 1 0...0
elementCount[4]   0 1 1 0 0...0
elementCount[5]   0 0 0 1 1...0
               -----------------block3 
               
      //while I think it would be most effecient to have each elementCount[i] be only the length of the max number in the Arr column, 
      //I ultimately decided against it since it wasn't necessary and I have been working on this a long time as it is.              
      */
      int elementCountRowCnt = 0;
      int arrRowCnt = 0;
      int[][] elementCount = new int[numOfColumns][128];
      arrLengthsCnt = 0;
      for(int i = 0; i < arrLengths[arrLengthsCnt][1]; i++)//O(i) where i is the number of rows. i * j = n so total O(n) //iterates through every elements only once
      {
         for(int j = 0; j < arrLengths[arrLengthsCnt][0]; j++)//O(j) where j is the number of columns (per block)
         {  
            elementCount[elementCountRowCnt][tempArr[arrRowCnt + j][i]]++;
         }
         elementCountRowCnt++;
         if(i == arrLengths[arrLengthsCnt][1] - 1 && arrLengths.length - 1 != arrLengthsCnt)
         {
            arrRowCnt += arrLengths[arrLengthsCnt][0];
            i = -1;
            arrLengthsCnt++;
         }
      }
      /*collectively adds up each row in elementCount
      so
         index:   0,1,2,3,4...128
         
elementCount[0]   0 0 0 1 0...0
               -----------------block1
elementCount[1]   0 2 0 0 0...0
elementCount[2]   0 0 1 1 0...0 
               -----------------block2
elementCount[3]   0 1 0 1 0...0
elementCount[4]   0 1 1 0 0...0
elementCount[5]   0 0 0 1 1...0
               -----------------block3 
               
               would produce:
               
         index:   0,1,2,3,4...128
         
elementCount[0]   0 0 0 1 1...1
               -----------------block1
elementCount[1]   0 2 2 2 2...2
elementCount[2]   0 0 1 2 2...2
               -----------------block2
elementCount[3]   0 1 1 2 2...2
elementCount[4]   0 1 2 2 2...2
elementCount[5]   0 0 0 1 2...2
               -----------------block3 

      //while each element needs to be subtracted by one, I decided to do this in the next step since I think it would require another loop otherwise.
      */
      for(int i = 0; i < elementCount.length; i++)//O(>n) so O(>n * 128)
      {
         for(int j = 1; j < 128; j++)//O(128)
         {
            elementCount[i][j] += elementCount[i][j - 1];
         }
      }
      /*
      This is where arr elemets are fully sorted. the elements of the columns will placed within their block location according to the elementCount array. When an element
      is placed, the value used in elementCount will by decremented by one. The product would be a fully sorted double array. 
      */
      arrLengthsCnt = arrLengths.length - 1;
      arrRowCnt = arr.length - 1;
      int countUp = elementCountRowCnt - arrLengths[arrLengthsCnt][1];
      for(int i = 0; i < arrLengths[arrLengthsCnt][1];i++)
      {
         for(int j = 0; j < arrLengths[arrLengthsCnt][0]; j++)
         {
            arr[elementCount[countUp][tempArr[arrRowCnt - j][i]] - 1 + arrRowCnt + 1 - arrLengths[arrLengthsCnt][0]] = tempArr[arrRowCnt - j];
            elementCount[countUp][tempArr[arrRowCnt - j][i]]--;
         }
         tempArr = arr.clone();
         elementCountRowCnt--;
         countUp++;
         if(i == arrLengths[arrLengthsCnt][1] - 1 && elementCountRowCnt > 0)
         {
            arrRowCnt -= arrLengths[arrLengthsCnt][0];
            arrLengthsCnt--;
            countUp = elementCountRowCnt - arrLengths[arrLengthsCnt][1];
            i = -1;
         }
      }
   }
     /*not my code V */
    // ---------------------------------------------------------------------
    // Do not change any of the code below!

    private static final int LabNo = 4;
    private static final String quarter = "Fall 2020";
    private static final Random rng = new Random(654321);

    private static boolean testProblem(byte[][] testCase)
    {
        byte[][] numbersCopy = new byte[testCase.length][];

        // Create copy.
        for (int i = 0; i < testCase.length; i++)
        {
            numbersCopy[i] = testCase[i].clone();
        }

        // Sort
        problem(testCase);
        Arrays.sort(numbersCopy, new numberComparator());

        // Compare if both equal
        if (testCase.length != numbersCopy.length)
        {
            return false;
        }

        for (int i = 0; i < testCase.length; i++)
        {
            if (testCase[i].length != numbersCopy[i].length)
            {
                return false;
            }

            for (int j = 0; j < testCase[i].length; j++)
            {
                if (testCase[i][j] != numbersCopy[i][j])
                {
                    return false;
                }
            }
        }

        return true;
    }

    // Very bad way of sorting.
    private static class numberComparator implements Comparator<byte[]>
    {
        @Override
        public int compare(byte[] n1, byte[] n2)
        {
            // Ensure equal length
            if (n1.length < n2.length)
            {
                byte[] tmp = new byte[n2.length];
                for (int i = 0; i < n1.length; i++)
                {
                    tmp[i] = n1[i];
                }
                n1 = tmp;
            }

            if (n1.length > n2.length)
            {
                byte[] tmp = new byte[n1.length];
                for (int i = 0; i < n2.length; i++)
                {
                    tmp[i] = n2[i];
                }
                n2 = tmp;
            }

            // Compare digit by digit.
            for (int i = n1.length - 1; i >=0; i--)
            {
                if (n1[i] < n2[i]) return -1;
                if (n1[i] > n2[i]) return 1;
            }

            return 0;
        }
    }

    public static void main(String args[])
    {
        System.out.println("CS 302 -- " + quarter + " -- Lab " + LabNo);
        testProblems();
    }

    private static void testProblems()
    {
        int noOfLines = 10000;

        System.out.println("-- -- -- -- --");
        System.out.println(noOfLines + " test cases.");

        boolean passedAll = true;

        for (int i = 1; i <= noOfLines; i++)
        {
            byte[][] testCase =  createTestCase(i);

            boolean passed = false;
            boolean exce = false;

            try
            {
                passed = testProblem(testCase);
            }
            catch (Exception ex)
            {
                passed = false;
                exce = true;
            }

            if (!passed)
            {
                System.out.println("Test " + i + " failed!" + (exce ? " (Exception)" : ""));
                passedAll = false;

                break;
            }
        }

        if (passedAll)
        {
            System.out.println("All test passed.");
        }

    }

    private static byte[][] createTestCase(int testNo)
    {
        int maxSize = Math.min(100, testNo) + 5;
        int size = rng.nextInt(maxSize) + 5;

        byte[][] numbers = new byte[size][];

        for (int i = 0; i < size; i++)
        {
            int digits = rng.nextInt(maxSize) + 1;
            numbers[i] = new byte[digits];

            for (int j = 0; j < digits - 1; j++)
            {
                numbers[i][j] = (byte)rng.nextInt(128);
            }

            // Ensures that the most significant digit is not 0.
            numbers[i][digits - 1] = (byte)(rng.nextInt(127) + 1);
        }

        return numbers;
    }

}
