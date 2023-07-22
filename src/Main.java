import java.util.Scanner;

//A class to represent the chromosomes
class Chromosome
{
    public int [] bits; //The bits that represent which items are chosen.
    public int price = 0;//A variable to store the fitness value of the function.

    Chromosome(int n)
    {
        bits = new int[n];
    }
}

public class Main {

    static Scanner input = new Scanner(System.in);
    static int population_size = 30;//Number of chromosomes in the population.
    static int t;//Number of test cases.
    static int w;//The weight of the knapsack in each test case.
    static int n;//The number of items in each test case.
    static int[] weights;//An array to store the weights of the items.
    static int[] prices;//An array to store the prices of the items.
    static Chromosome[] population = new Chromosome[population_size];//The current population.
    static Chromosome[] parents = new Chromosome[population_size];//An array containing the parents chosen at the end of the selection process.
    static double Pc = 0.6;//The probability of crossover.
    static Chromosome[] offsprings = new Chromosome[population_size];//An array containing the offsprings.
    static double Pm = 0.1;//The probability of mutation.

    public static void Initialization()//The function that will initialize the first generation.
    {
        for (int i = 0; i < population_size; i++)
        {
            population[i] = new Chromosome(n);
            parents[i] = new Chromosome(n);
            offsprings[i] = new Chromosome(n);
            for (int j = 0; j < n; j++)
            {
                population[i].bits[j] = (int) (2 * Math.random());
            }
        }
    }

    public static void Fitness()//The Function that calculates the fitness of each chromosome.
    {
        int total_weight = 0;

        for (int i = 0; i < population_size; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (population[i].bits[j] == 1)
                    total_weight += weights[j];
                if (population[i].bits[j] == 1)
                    population[i].price += prices[j];
            }
            //Handling the infeasible chromosomes.
            if (total_weight > w)
                population[i].price = 0;

            total_weight = 0;
        }
    }

    public static void Selection()//The function that will select the parent chromosomes. We will use the roulette-wheel method.
    {
        int total_fitness = 0;
        for (int i = 0; i < population_size; i++)
        {
            total_fitness += population[i].price;
        }

        for (int i = 0; i < population_size; i++)
        {
            int selector = (int) (total_fitness * Math.random());
            for (int j = 0; j < population_size; j++)
            {
                selector -= population[j].price;
                if (selector <= 0)
                {
                    parents[i].bits = population[j].bits.clone();
                    break;
                }
            }
        }
    }

    public static void Crossover()//The crossover function that will produce the offsprings.
    {
        for (int i = 0; i < population_size; i+=2)
        {
            double decision = Math.random();
            if (decision > Pc)
            {
                offsprings[i].bits = parents[i].bits.clone();
                offsprings[i+1].bits = parents[i+1].bits.clone();
            }

            else
            {
                int point = (int) (n * Math.random());
                if (point == 0)
                    point = 1;
                System.arraycopy(parents[i].bits,0,offsprings[i].bits,0,point);
                System.arraycopy(parents[i+1].bits,0,offsprings[i+1].bits,0,point);

                System.arraycopy(parents[i+1].bits,point,offsprings[i].bits,point, n - point);
                System.arraycopy(parents[i].bits,point,offsprings[i+1].bits,point, n - point);
            }
        }
    }

    public static void Mutation()//The Mutation function.
    {
        for (int i = 0; i < population_size; i++)
        {
            for (int j = 0; j < n; j++)
            {
                double decision = Math.random();
                if (decision <= Pm)
                {
                    if (offsprings[i].bits[j] == 1)
                        offsprings[i].bits[j] = 0;
                    else
                        offsprings[i].bits[j] = 1;
                }
            }
        }
    }

    static void Replacement()//The replacement function.Here we will use full replacement.
    {
        for (int i = 0; i < population_size; i++)
        {
            population[i].bits = offsprings[i].bits.clone();
            population[i].price = 0;
        }
    }

    static void Output()//The function that will print the output.
    {
        Chromosome best = new Chromosome(n);
        int num_selected = 0;

        for(int i = 0; i < population_size; i++)
        {
            if (population[i].price > best.price)
            {
                best = population[i];
            }
        }

        for (int i = 0; i < n; i++)
        {
            if (best.bits[i] == 1)
                num_selected ++;
        }

        System.out.println("Number of selected items is " + num_selected);
        System.out.println("The selected items are:");

        for (int i = 0; i < n; i++)
        {
            if (best.bits[i] == 1)
            {
                System.out.println("Item #" + (i+1));
                System.out.println("weight: " + weights[i]);
                System.out.println("price: " + prices[i]);
            }
        }
        System.out.println("The total price: " + best.price);
        System.out.println();
    }

    public static void main(String[] args) {

        t = input.nextInt();
        for (int j = 0; j < t; j++)
        {
            System.out.println("In test case #" + (j+1));
            w = input.nextInt();
            n = input.nextInt();
            weights = new int[n];
            prices  = new int[n];
            for (int i = 0; i < n; i++)
            {
                weights[i] = input.nextInt();
                prices[i] = input.nextInt();
            }

            Initialization();

            for (int i = 0; i < 5; i++)
            {

                Fitness();

                Selection();

                Crossover();

                Mutation();

                Replacement();

            }

            Fitness();

            Output();
        }

    }
}
