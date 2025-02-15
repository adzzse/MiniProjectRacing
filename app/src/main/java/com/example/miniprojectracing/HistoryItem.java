//package com.example.miniprojectracing;
//
//public class HistoryItem {
//    private String horseName;
//    private int totalRaces;
//    private int totalWins;
//    private double winRate;
//
//    public HistoryItem(String horseName, int totalRaces, int totalWins, double winRate) {
//        this.horseName = horseName;
//        this.totalRaces = totalRaces;
//        this.totalWins = totalWins;
//        this.winRate = winRate;
//    }
//
//    public String getHorseName() {
//        return horseName;
//    }
//
//    public int getTotalRaces() {
//        return totalRaces;
//    }
//
//    public int getTotalWins() {
//        return totalWins;
//    }
//
//    public double getWinRate() {
//        return winRate;
//    }
//}


package com.example.miniprojectracing;

public class HistoryItem {
    private String horseName;
    private int totalRaces;
    private int totalWins;
    private double winRate;

    public HistoryItem(String horseName, int totalRaces, int totalWins, double winRate) {
        this.horseName = horseName;
        this.totalRaces = totalRaces;
        this.totalWins = totalWins;
        this.winRate = winRate;
    }

    public String getHorseName() {
        return horseName;
    }

    public int getTotalRaces() {
        return totalRaces;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public double getWinRate() {
        return winRate;
    }
}
