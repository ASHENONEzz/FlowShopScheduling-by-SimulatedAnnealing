
package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SimulatedAnnealing {
    public static void main(String[] args) {

        //定义工件和机器数
        int[] n = new int[11];
        int[] m = new int[11];

        //定义加工时间
        ArrayList<int[][]> processing_time = new ArrayList<>();

        //读取用例并处理数据
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\Ashen\\Desktop\\最优化方法\\最优化方法大作业-2023-置换流水车间调度-含测试用例11道题目.txt"));
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 2; j++) {
                    br.readLine();
                }
                String[] mandn = br.readLine().split(" ");
                n[i] = Integer.parseInt(mandn[0]);
                m[i] = Integer.parseInt(mandn[1]);
                int [][] Processing_Time = new int[n[i]][m[i]];
                for (int j = 0; j < n[i]; j++) {
                    String[] newLine = br.readLine().split(" ");
                    for (int k = 0; k < m[i]; k++) {
                        Processing_Time[j][k] = Integer.parseInt(newLine[2*k+1]);
                    }
                }
                processing_time.add(Processing_Time);
            }
            br.close();
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //依次计算每个用例
        for (int i = 0; i < 11; i++) {
            long stime = System.currentTimeMillis();

            System.out.println("Instance " + i + ":");
            anneal(n[i], processing_time.get(i));

            long etime = System.currentTimeMillis();
            System.out.printf("Execution time: %d ms", (etime - stime));
            System.out.println();
        }

    }

    // 计算每个工件在每台机器上的完成时间
    private static int calculateCompletionTime(int[] schedule, int[][] processing_time) {
        int n = schedule.length;
        int m = processing_time[0].length;
        int[][] completion_time = new int[n+1][m+1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                //completion_time[i][j]表示第i个工件在第j个机器完成加工的总时间（包括之前阶段的时间）
                //根据甘特图这应该是一个类似于动态规划的计算过程，否则会出现一台机器同时加工两个工件的情况
                completion_time[i][j] = processing_time[schedule[i-1]][j-1] + Math.max(completion_time[i - 1][j], completion_time[i][j - 1]);
            }
        }
        return completion_time[n][m];
    }

    // 随机打乱数组顺序
    private static void shuffle(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            int tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    //模拟退火并输出结果
    private static void anneal(int n, int[][] processing_time) {
        // 定义初始温度和终止条件
        double initial_temperature = 10000;
        double stopping_temperature = 1e-8;
        double cooling_rate = 0.95;
        int iterations = 1000;

        // 随机生成初始解
        int[] current_schedule = new int[n];
        for (int i = 0; i < n; i++) {
            current_schedule[i] = i;
        }
        shuffle(current_schedule);
        int current_cost = calculateCompletionTime(current_schedule, processing_time);

        // 开始模拟退火
        double temperature = initial_temperature;
        while (temperature > stopping_temperature) {
            for (int i = 0; i < iterations; i++) {
                // 产生邻域解
                int[] new_schedule = current_schedule.clone();
                int j = new Random().nextInt(n);
                int k = new Random().nextInt(n);
                int tmp = new_schedule[j];
                new_schedule[j] = new_schedule[k];
                new_schedule[k] = tmp;
                int new_cost = calculateCompletionTime(new_schedule, processing_time);
                // Metropolis准则
                if (new_cost < current_cost) {
                    current_schedule = new_schedule;
                    current_cost = new_cost;
                } else {
                    double delta = new_cost - current_cost;
                    double probability = Math.exp(-delta / temperature);
                    if (new Random().nextDouble() < probability) {
                        current_schedule = new_schedule;
                        current_cost = new_cost;
                    }
                }
            }
            // 降温
            temperature *= cooling_rate;
        }

        // 输出最优解和总完工时间
        System.out.println("Optimal Schedule: " + Arrays.toString(current_schedule));
        System.out.println("Total Completion Time: " + current_cost);
    }
}

//以上代码中的模拟退火算法实现了以下步骤：
//
//        1. 随机生成一个初始解，并计算其总完工时间。
//        2. 对当前解进行若干次随机扰动，产生邻域解，并计算邻域解的总完工时间。
//        3. 根据Metropolis准则接受或拒绝邻域解，更新当前解。
//        4. 降温并重复步骤2-3，直到温度达到终止条件。
//        5. 输出最优解和总完工时间。
//
//        在计算总完工时间时，我们使用了流水线调度问题的经典算法，即计算每个工件在
//        每台机器上的完成时间，然后取最后一台机器上的最大完成时间作为总完工时间。
//        在本算法中，我们使用了一个二维数组completion_time来记录每个工件在每台
//        机器上的完成时间，并根据这个数组计算总完工时间。
//
//        此外，我们还实现了一个shuffle方法来随机打乱数组顺序，以产生随机的初始解。
//
//        以上代码只是一个简单的示例，实际应用中可能需要根据具体问题进行修改和优化。

