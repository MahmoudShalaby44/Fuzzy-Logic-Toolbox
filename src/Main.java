import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Variable
{
    String name;
    String type;
    int upperBound;
    int lowerBound;
    double crisp_value;
    ArrayList<FuzzySet> fuzzy_sets = new ArrayList<>();
}

class FuzzySet
{
    String name;
    String type;
    int a;
    int b;
    int c;
    int d;
    double degree;
}

class Point
{
    double x;
    double y;
}

public class Main {

   static Scanner inp;

    static {
        try {
            inp = new Scanner(new File("Data.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String system_name;
    static String system_description;
    static ArrayList<Variable> variables = new ArrayList<>();
    static ArrayList<String> rules = new ArrayList<>();

    public static void AddVariables()
    {
        System.out.println("Enter the variable’s name, type (IN/OUT) and range ([lower, upper]):");
        System.out.println("(Press x to finish)");

        while (true)
        {
            String query = inp.nextLine();
            System.out.println(query);

            if (query.equals("x"))
            {
                break;
            }

            String word = "";
            Character c;
            int index = 0;
            Variable v = new Variable();

            for (int i = 0; i < query.length(); i++)
            {
                c = query.charAt(i);
                if (c == ' ' || c == '\t')
                {
                    break;
                }

                word += c;
                index++;
            }

            v.name = word;
            word = "";

            while (query.charAt(index) == ' ' || query.charAt(index) == '\t')
            {
                index++;
            }

            for (int i = index; i < query.length(); i++)
            {
                c = query.charAt(i);
                if (c == ' ' || c == '\t')
                {
                    break;
                }

                word += c;
                index++;
            }

            v.type = word;
            word = "";

            while (query.charAt(index) == ' ' || query.charAt(index) == '\t')
            {
                index++;
            }

            for (int i = index; i < query.length(); i++)
            {
                c = query.charAt(i);
                if (c == ' ' || c == '\t')
                {
                    continue;
                }

                word += c;
                index++;
            }

            String[] ranges = word.split(",");
            String lower = ranges[0].substring(1);
            String upper = ranges[1].substring(0, ranges[1].length() - 1);

            v.lowerBound = Integer.parseInt(lower);
            v.upperBound = Integer.parseInt(upper);

            variables.add(v);
        }
    }

    public static void AddFuzzySets()
    {
        System.out.println("Enter the variable’s name:");
        System.out.println("--------------------------");

        String n = inp.nextLine();
        System.out.println(n);
        int ind = -1;

        for (int i = 0; i < variables.size(); i++)
        {
            if (variables.get(i).name.equals(n))
            {
                ind = i;
            }
        }

        System.out.println();
        System.out.println("Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)");
        System.out.println("-----------------------------------------------------");

        while (true)
        {
            String query = inp.nextLine();
            System.out.println(query);

            if (query.equals("x"))
            {
                break;
            }

            String word = "";
            Character c;
            int index = 0;
            FuzzySet fuzzy = new FuzzySet();

            for (int i = 0; i < query.length(); i++)
            {
                c = query.charAt(i);
                if (c == ' ' || c == '\t')
                {
                    break;
                }

                word += c;
                index++;
            }

            fuzzy.name = word;
            word = "";

            while (query.charAt(index) == ' ' || query.charAt(index) == '\t')
            {
                index++;
            }

            for (int i = index; i < query.length(); i++)
            {
                c = query.charAt(i);
                if (c == ' ' || c == '\t')
                {
                    break;
                }

                word += c;
                index++;
            }

            fuzzy.type = word;
            word = "";

            while (query.charAt(index) == ' ' || query.charAt(index) == '\t')
            {
                index++;
            }

            for (int i = index; i < query.length(); i++)
            {
                c = query.charAt(i);
                word += c;
                index++;
            }

            String[] points = word.split(" ");
            fuzzy.a = Integer.parseInt(points[0]);
            fuzzy.b = Integer.parseInt(points[1]);
            fuzzy.c = Integer.parseInt(points[2]);

            if (fuzzy.type.equals("TRAP"))
                fuzzy.d = Integer.parseInt(points[3]);

            variables.get(ind).fuzzy_sets.add(fuzzy);
        }
    }

    public static void AddRules()
    {
        System.out.println("Enter the rules in this format: (Press x to finish)");
        System.out.println("IN_variable set operator IN_variable set => OUT_variable set");

        while (true)
        {
            String query = inp.nextLine();
            System.out.println(query);
            if (query.equals("x"))
                break;
            rules.add(query);
        }
    }

   public static void Fuzzification(Variable variable)
   {
       for (int i = 0; i < variable.fuzzy_sets.size(); i++)
       {

           Point p1 = new Point();
           Point p2 = new Point();

           if ((variable.crisp_value >= variable.fuzzy_sets.get(i).a) && (variable.crisp_value <= variable.fuzzy_sets.get(i).c))
           {
               if ((variable.crisp_value >= variable.fuzzy_sets.get(i).a) && (variable.crisp_value <= variable.fuzzy_sets.get(i).b))
               {
                   p1.x = variable.fuzzy_sets.get(i).a;
                   p1.y = 0;

                   p2.x = variable.fuzzy_sets.get(i).b;
                   p2.y = 1;
               }

               else
               {
                   if (variable.fuzzy_sets.get(i).type.equals("TRI"))
                   {
                       p1.x = variable.fuzzy_sets.get(i).b;
                       p1.y = 1;

                       p2.x = variable.fuzzy_sets.get(i).c;
                       p2.y = 0;
                   }

                   else
                   {
                       p1.x = variable.fuzzy_sets.get(i).b;
                       p1.y = 1;

                       p2.x = variable.fuzzy_sets.get(i).c;
                       p2.y = 1;
                   }
               }

               double m = (p2.y - p1.y)/(p2.x - p1.x);
               double c = p1.y - (m * p1.x);
               variable.fuzzy_sets.get(i).degree = (variable.crisp_value * m) + c;
           }

           else if ((variable.fuzzy_sets.get(i).type.equals("TRAP")) && (variable.crisp_value >= variable.fuzzy_sets.get(i).c) && (variable.crisp_value <= variable.fuzzy_sets.get(i).d))
           {
               p1.x = variable.fuzzy_sets.get(i).c;
               p1.y = 1;

               p2.x = variable.fuzzy_sets.get(i).d;
               p2.y = 0;

               double m = (p2.y - p1.y)/(p2.x - p1.x);
               double c = p1.y - (m * p1.x);
               variable.fuzzy_sets.get(i).degree = (variable.crisp_value * m) + c;
           }

           else
           {
               variable.fuzzy_sets.get(i).degree = 0;
           }
       }
   }

   public static void Inference(String rule)
   {
       int index = -1;
       int ind = -1;
       boolean nott = false;
       int i = 0;
       double val1 = -1;
       double val2 = -1;
       String operator;
       double val3;

       String[] words = rule.split(" ");

       while (true)
       {
           if (!(words[i].equals("_not")))
               break;
           else
           {
               nott = !nott;
               i++;
           }
       }

       for (int j = 0; j < variables.size(); j++)
       {
           if (variables.get(j).name.equals(words[i]))
           {
               for (int k = 0; k < variables.get(j).fuzzy_sets.size(); k++)
               {
                   if (variables.get(j).fuzzy_sets.get(k).name.equals(words[i+1]))
                   {
                       val1 = variables.get(j).fuzzy_sets.get(k).degree;
                       if (nott)
                           val1 = 1 - val1;
                       break;
                   }
               }
           }
       }

       i += 2;
       nott = false;

       while (true)
       {
           if (!(words[i].equals("_not")))
               break;
           else
           {
               nott = !nott;
               i++;
           }
       }

       operator = words[i];

       if (!(words[i].equals("_not")) && (words[i].contains("_not")))
       {
           nott = !nott;
           operator = words[i].substring(0,words[i].length() - 4);
       }

       i++;

       for (int j = 0; j < variables.size(); j++)
       {
           if (variables.get(j).name.equals(words[i]))
           {
               for (int k = 0; k < variables.get(j).fuzzy_sets.size(); k++)
               {
                   if (variables.get(j).fuzzy_sets.get(k).name.equals(words[i+1]))
                   {
                       val2 = variables.get(j).fuzzy_sets.get(k).degree;
                       if (nott)
                           val2 = 1 - val2;
                       break;
                   }
               }
           }
       }

       i += 3;
       nott = false;

       for (int j = 0; j < variables.size(); j++)
       {
           if (variables.get(j).name.equals(words[i]))
           {
               index = j;
               for (int k = 0; k < variables.get(j).fuzzy_sets.size(); k++)
               {
                   if (variables.get(j).fuzzy_sets.get(k).name.equals(words[i+1]))
                   {
                       ind = k;
                   }
               }
           }
       }

       if (operator.equals("and"))
       {
           val3 = Math.min(val1,val2);
       }

       else
       {
           val3 = Math.max(val1,val2);
       }

       if (variables.get(index).fuzzy_sets.get(ind).degree < val3)
           variables.get(index).fuzzy_sets.get(ind).degree = val3;

   }

   public static void Defuzzification() throws IOException {
       double numerator = 0;
       double denominator = 0;

       for (int i = 0; i < variables.size(); i++)
       {
           if (variables.get(i).type.equals("OUT"))
           {
               for (int j = 0; j < variables.get(i).fuzzy_sets.size(); j++)
               {
                   double centroid;
                   if (variables.get(i).fuzzy_sets.get(j).type.equals("TRI"))
                       centroid = (variables.get(i).fuzzy_sets.get(j).a + variables.get(i).fuzzy_sets.get(j).b + variables.get(i).fuzzy_sets.get(j).c)/3.0;
                   else
                       centroid = (variables.get(i).fuzzy_sets.get(j).b + variables.get(i).fuzzy_sets.get(j).c)/2.0;
                   numerator += (variables.get(i).fuzzy_sets.get(j).degree) * centroid;
                   denominator += variables.get(i).fuzzy_sets.get(j).degree;
               }
               variables.get(i).crisp_value = numerator / denominator;
               variables.get(i).crisp_value = Math.round(100 * variables.get(i).crisp_value) / 100.0;
               Fuzzification(variables.get(i));
               double max = -1;
               int index = -1;

               for (int j = 0; j < variables.get(i).fuzzy_sets.size(); j++)
               {
                   if (variables.get(i).fuzzy_sets.get(j).degree >= max)
                   {
                       max = variables.get(i).fuzzy_sets.get(j).degree;
                       index = j;
                   }
               }

               FileWriter fw = new FileWriter("output.txt",false);

               fw.write("The predicted " + variables.get(i).name + " is " + variables.get(i).fuzzy_sets.get(index).name +  " (" + variables.get(i).crisp_value + ")");

               fw.close();

               System.out.println("The predicted " + variables.get(i).name + " is " + variables.get(i).fuzzy_sets.get(index).name +  " (" + variables.get(i).crisp_value + ")");
           }
       }
   }

   public static void MainMenu() {
       System.out.println();
       System.out.println("Main Menu:");
       System.out.println("==========");
       System.out.println("1- Add variables.");
       System.out.println("2- Add fuzzy sets to an existing variable.");
       System.out.println("3- Add rules.");
       System.out.println("4- Run the simulation on crisp values.");

       String action = inp.nextLine();
       System.out.println(action);

       if (action.equalsIgnoreCase("Close"))
           ToolBox();

       else
       {
           int num = Integer.parseInt(action);

           switch (num)
           {
               case 1:
                   System.out.println();
                   AddVariables();
                   MainMenu();
                   break;

               case 2:
                   System.out.println();
                   AddFuzzySets();
                   MainMenu();
                   break;

               case 3:
                   System.out.println();
                   AddRules();
                   MainMenu();
                   break;

               case 4:
                   if (rules.size() == 0 || variables.get(0).fuzzy_sets.size() == 0)
                       System.out.println("CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.");

                   else
                   {
                       System.out.println();
                       System.out.println("Enter the crisp values:");
                       System.out.println("-----------------------");

                        for (int i = 0; i < variables.size(); i++)
                        {
                            if (variables.get(i).type.equals("IN"))
                            {
                                System.out.print(variables.get(i).name +": ");
                                variables.get(i).crisp_value = inp.nextInt();
                                System.out.print(variables.get(i).crisp_value);
                                inp.nextLine();
                                System.out.println();
                            }
                        }

                       System.out.println("Running the simulation…");

                        for (int i = 0; i < variables.size(); i++)
                        {
                            if (variables.get(i).type.equals("IN"))
                                Fuzzification(variables.get(i));
                        }

                       System.out.println("Fuzzification => done");

                       for (int i = 0; i < rules.size(); i++)
                       {
                           Inference(rules.get(i));
                       }

                       System.out.println("Inference => done");

                       System.out.println("Defuzzification => done");

                       System.out.println();

                       try {
                           Defuzzification();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   MainMenu();
                   break;

               default:
                   System.out.println("Please enter only one of the numbers that are available.");
                   MainMenu();
           }
       }

   }


   public static void ToolBox()
    {
        System.out.println();
        System.out.println("Fuzzy Logic Toolbox");
        System.out.println("===================");
        System.out.println("1- Create a new fuzzy system");
        System.out.println("2- Quit");

        int n = inp.nextInt();
        System.out.println(n);
        inp.nextLine();

        switch (n)
        {
            case 1:
                System.out.println();
                System.out.println("Enter the system’s name and a brief description:");
                System.out.println("------------------------------------------------");
                system_name = inp.nextLine();
                System.out.println(system_name);
                system_description = inp.nextLine();
                System.out.println(system_description);
                MainMenu();
                break;

            case 2:
                System.exit(0);

            default:
                System.out.println("Please enter only one of the numbers that are available.");
                ToolBox();
        }
    }


    public static void main(String[] args)
    {
        ToolBox();

    }
}
