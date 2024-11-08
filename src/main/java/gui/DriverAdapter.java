package gui;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import domain.Driver;
import domain.Ride;

public class DriverAdapter extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private List<Ride> rides;
    private final String[] columnNames = {"From", "To", "Date", "Places", "Price"};

    public DriverAdapter(Driver driver) {
        this.rides = driver.getCreatedRides();
    }

    @Override
    public int getRowCount() {
        return rides.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ride ride = rides.get(rowIndex);
        switch (columnIndex) {
            case 0: return ride.getFrom();
            case 1: return ride.getTo();
            case 2: return ride.getDate();
            case 3: return ride.getnPlaces();
            case 4: return ride.getPrice();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}