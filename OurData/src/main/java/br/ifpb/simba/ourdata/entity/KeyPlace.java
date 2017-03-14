package br.ifpb.simba.ourdata.entity;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Objects;

/**
 * Class to represent a KeyPlace, this class save some meta-dates of a one Place
 * find into a one Resource
 *
 * @version 1.0
 * @author Pedro Arthur, Wensttay de Sousa Alencar <yattsnew@gmail.com>
 * @date 07/01/2017 - 12:01:31
 */
public class KeyPlace {

    private int repeatNumber;
    private int rowsNumber;
    private int columNumber;
    private String columValue;
    private Timestamp metadataCreated;
    private String idResource;
    private Place place;

    /**
     * @return the repeatNumber
     */
    public int getRepeatNumber() {
        return repeatNumber;
    }

    /**
     * @param repeatNumber the repeatNumber to set
     */
    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    /**
     * @return the rowsNumber
     */
    public int getRowsNumber() {
        return rowsNumber;
    }

    /**
     * @param rowsNumber the rowsNumber to set
     */
    public void setRowsNumber(int rowsNumber) {
        this.rowsNumber = rowsNumber;
    }

    /**
     * @return the columNumber
     */
    public int getColumNumber() {
        return columNumber;
    }

    /**
     * @param columNumber the columNumber to set
     */
    public void setColumNumber(int columNumber) {
        this.columNumber = columNumber;
    }

    /**
     * @return the columValue
     */
    public String getColumValue() {
        return columValue;
    }

    /**
     * @param columValue the columValue to set
     */
    public void setColumValue(String columValue) {
        this.columValue = columValue;
    }

    /**
     * @return the metadataCreated
     */
    public Timestamp getMetadataCreated() {
        return metadataCreated;
    }

    /**
     * @param metadataCreated the metadataCreated to set
     */
    public void setMetadataCreated(Timestamp metadataCreated) {
        this.metadataCreated = metadataCreated;
    }

    /**
     * @return the idResource
     */
    public String getIdResource() {
        return idResource;
    }

    /**
     * @param idResource the idResource to set
     */
    public void setIdResource(String idResource) {
        this.idResource = idResource;
    }

    /**
     * @return the place
     */
    public Place getPlace() {
        return place;
    }

    /**
     * @param place the place to set
     */
    public void setPlace(Place place) {
        this.place = place;
    }

    /**
     * @return the comparadorByName
     */
    public static Comparator<KeyPlace> getComparadorByName() {
        return comparadorByName;
    }

    /**
     * @param aComparadorByName the comparadorByName to set
     */
    public static void setComparadorByName(Comparator<KeyPlace> aComparadorByName) {
        comparadorByName = aComparadorByName;
    }

    /**
     * Return if this KeyPlace is equals the KayPlace passed param
     *
     * @param obj KeyPlace wants to compare
     *
     * @return
     */
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
        final KeyPlace other = (KeyPlace) obj;
        if (!Objects.equals(this.columNumber, other.columNumber)) {
            return false;
        }
        if (!Objects.equals(this.columValue, other.columValue)) {
            return false;
        }
        if (!Objects.equals(this.idResource, other.idResource)) {
            return false;
        }
        if (this.getPlace() == null || other.getPlace() == null) {
            return false;
        }
        if (!Objects.equals(this.place.getId(), other.place.getId())) {
            return false;
        }
        return true;
    }

    /**
     * Instancie a comparator usign a Name's KeyPlace
     * <p>
     */
    private static Comparator<KeyPlace> comparadorByName = new Comparator<KeyPlace>() {
        @Override
        public int compare(KeyPlace s1, KeyPlace s2) {
            return s1.getPlace().getNome().compareTo(s2.getPlace().getNome());
        }
    };

    /**
     * Representation of this KeyPlace on String formate
     *
     * @return String with all values into this KeyPlace
     */
    @Override
    public String toString() {
        return "KeyPlace{" + "repeatNumber=" + repeatNumber + ", rowsNumber=" + rowsNumber + ", columNumber=" + columNumber + ", columValue=" + columValue + ", metadataCreated=" + metadataCreated + ", idResource=" + idResource + ", place=" + place.toString() + '}';
    }
}
