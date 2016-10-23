/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifpb.simba.ourdata.reader;

import br.ifpb.simba.ourdata.entity.KeyTime;
import br.ifpb.simba.ourdata.entity.Period;
import br.ifpb.simba.ourdata.entity.utils.PeriodUtils;
import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jdom.JDOMException;

/**
 *
 * @author kieckegard
 */
public class KeyTimeBo {

    public static final int NUM_ROWS_CHECK_DEFAULT = 10;
    public static final int NO_COLUM_ID = -99;

    private PeriodUtils periodUtils;
    private int numRowsCheck;

    public KeyTimeBo() {
        this.numRowsCheck = NUM_ROWS_CHECK_DEFAULT;
        periodUtils = new PeriodUtils();
    }

    public KeyTimeBo(int numRowsCheck) {
        this.numRowsCheck = numRowsCheck;
        periodUtils = new PeriodUtils();
    }

    public KeyTimeBo(int numRowsCheck, String properties_path) {
        this.numRowsCheck = numRowsCheck;
        periodUtils = new PeriodUtils(properties_path);
    }

    public int getNumRowsCheck() {
        return this.numRowsCheck;
    }

    public void setNumRowsCheck(int numRowsCheck) {
        this.numRowsCheck = numRowsCheck;
    }

    public List<KeyTime> getKeyTimes(CkanResource resource, CkanDataset dataset) throws IOException, JDOMException, DocumentCreationTimeMissingException {

        float percent = 0;

        List<KeyTime> resultKeyTimes = new ArrayList<>();
        String resourceId = resource.getId();
        String resourceUrl = resource.getUrl();

        CSVReaderOD cSVReaderOD = new CSVReaderOD();

        //get a List which contains all csv content
        List<String[]> csvRows = cSVReaderOD.build(resourceUrl);

        if (csvRows == null) {
            csvRows = new ArrayList<>();
        }

        int csvRowsSize = csvRows.size();
        System.out.println("File Row Size: " + csvRowsSize + " |||| ");

        KeyTime placeByDescriptions = getTimeByDescriptions(resource, csvRowsSize, dataset);

//        if (placeByDescriptions != null) {
//            resultKeyTimes.add(placeByDescriptions);
//            return resultKeyTimes;
//        }
        //Iterating all csvRows
        for (int rowIndex = 1; rowIndex < csvRowsSize; rowIndex++) {

            //getting current row
            String[] row = csvRows.get(rowIndex);

            //creating current row keyplace list
            List<Period> rowPeriods = new ArrayList<>();
            //Iterating each csvRow's columns
            for (int colIndex = 0; colIndex < row.length; colIndex++) {

//              Dentro desse comando se faz o filtro para a lista de colunas que apresentaram
//              resutados encontrados na pesquisa no Gazetteer
                if (resultKeyTimes.size() > numRowsCheck) {
                    boolean validColumNumber = false;
                    while (colIndex < row.length) {
                        for (int i = 0; i < numRowsCheck; i++) {
                            if (resultKeyTimes.get(i).getPeriod().getEndDate().getCol() == colIndex
                                    || resultKeyTimes.get(i).getPeriod().getStartDate().getCol() == colIndex) {
                                validColumNumber = true;
                                break;
                            }
                        }

                        if (validColumNumber) {
                            break;
                        } else {
                            ++colIndex;
                        }
                    }
                }

                if (colIndex >= row.length) {
                    break;
                }

                String colValue = row[colIndex].replace("\n", " ");
                colValue = colValue.trim();

                if (colValue != null && !colValue.equals("")) {

                    Date timeBase = null;
                    Timestamp created = resource.getCreated();
                    if (created != null) {
                        timeBase = new Date(created.getTime());
                    }
                    if (timeBase == null) {
                        timeBase = new Date();
                    }

                    Period findPeriod = periodUtils.findPeriod(colValue, colIndex, timeBase);
                    if (placeByDescriptions != null
                            && findPeriod != null
                            && findPeriod.intersect(placeByDescriptions.getPeriod())) {
                        rowPeriods.add(findPeriod);
                    } else if (findPeriod != null) {
                        // Verificar com Fábio: 
                        // O que fazer caso não encontre nenhum Periodo 
                        // nos titulos para comprar com os encontrado internamente?
                        rowPeriods.add(findPeriod);
                    }
                }

            } //ends col iteration

            if (!rowPeriods.isEmpty()) {
                Period joinPeriods = periodUtils.joinPeriods(rowPeriods);
                KeyTime preencherKeyTime = preencherKeyTime(csvRowsSize, resourceId, joinPeriods, 1);
                resultKeyTimes.add(preencherKeyTime);
            }

            /*
             * If there's no places found in the csv file and we already verify
             * the whole thing until row 10,
             * I guess the csv does not contains any place xD
             */
            if (rowIndex >= numRowsCheck && resultKeyTimes.isEmpty()) {
                if (placeByDescriptions != null) {
                    resultKeyTimes.add(placeByDescriptions);
                }
                System.out.println(TextColor.ANSI_RED.getCode() + " " + "ERRO: ATINGIU O NUMERO MAX DE " + numRowsCheck + " ROWS VERIFICADAS SEM ENCONTRAR NENHUMA KEYPLACE !!");
                break;
            }

            //percent feedback
            percent = percentFeedback(resultKeyTimes, rowIndex, csvRowsSize, percent);

        } //ends rows iteration

        if (!resultKeyTimes.isEmpty()) {
            System.out.println("100 %");
        }

        cSVReaderOD.closeAll();
        return resultKeyTimes;
    }

    private float percentFeedback(List<KeyTime> keyTimes, int rowIndex, int csvRowsSize, Float percent) {
        if (!keyTimes.isEmpty()) {
            NumberFormat formatter = new DecimalFormat("#0.00");
            float percentRead = (((float) rowIndex * 100) / (float) csvRowsSize);

            if (percent + 10 < percentRead) {

                System.out.println(formatter.format(percentRead) + " %");
                return percentRead;
            }
        }

        return percent;
    }

    private KeyTime preencherKeyTime(int csvRowsSize, String resourceId, Period period, int repeatNumber) {

        KeyTime keyTime = new KeyTime();
        keyTime.setIdResource(resourceId);
        keyTime.setMetadataCreated(new Timestamp(System.currentTimeMillis()));
        keyTime.setPeriod(period);
        keyTime.setRepeatNumber(repeatNumber);
        keyTime.setRowsNumber(csvRowsSize);

        return keyTime;
    }

    private KeyTime getTimeByDescriptions(CkanResource resource, int rowsSize, CkanDataset dataset) throws IOException, JDOMException, DocumentCreationTimeMissingException {

        String resourceName = resource.getName();
        String resourceDescription = resource.getDescription();
        String datasetName = dataset.getName();
        String datasetNotes = dataset.getNotes();

        Period period = null;

        Date timeBase = null;
        Timestamp created = resource.getCreated();

        if (created != null) {
            timeBase = new Date(created.getTime());
        }

        if (timeBase == null) {
            return null;
        }

        period = periodUtils.findPeriod(resourceName, NO_COLUM_ID, timeBase);

        if (period == null) {
            period = periodUtils.findPeriod(resourceDescription, NO_COLUM_ID, timeBase);
        }
        if (period == null) {
            period = periodUtils.findPeriod(datasetName, NO_COLUM_ID, timeBase);
        }
        if (period == null) {
            period = periodUtils.findPeriod(datasetNotes, NO_COLUM_ID, timeBase);
        }
        if (period == null) {
            return null;
        }

        return preencherKeyTime(rowsSize, resource.getId(), period, rowsSize);
    }
}
