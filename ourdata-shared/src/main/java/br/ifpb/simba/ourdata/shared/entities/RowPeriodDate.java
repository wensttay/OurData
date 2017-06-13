package br.ifpb.simba.ourdata.shared.entities;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @version 1.0
 * @author Wensttay de Sousa Alencar <yattsnew@gmail.com>
 * @date 07/01/2017 - 12:01:31
 */
public class RowPeriodDate {

    private Date date;
    private int col;

    public RowPeriodDate() {
    }

    public RowPeriodDate(Date date) {
        this.date = date;
    }

    public RowPeriodDate(Date date, int col) {
        this.date = date;
        this.col = col;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.date);
        hash = 67 * hash + this.col;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RowPeriodDate other = (RowPeriodDate) obj;
        if (this.col != other.col) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

}
