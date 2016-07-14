/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ifpb.simba.ourdata.dao.entity;

import br.ifpb.simba.ourdata.dao.GenericBdDao;
import br.ifpb.simba.ourdata.entity.KeyPlace;
import br.ifpb.simba.ourdata.entity.Place;
import br.ifpb.simba.ourdata.entity.Resource;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kieckegard
 */
public class ResourceBdDao extends GenericBdDao {

    private PreparedStatement pstm;

    public List<Resource> getResourcesIntersectedBy(Place placeToSearch) {
        List<Resource> resources = new ArrayList<>();

//        String sql1 = "SELECT r.id, r.description, r.format, r.url, r.id_dataset, rp.repeat_number, rp.rows_number, rp.colum_value, \n"
//                + "rp.metadata_Created, rp.minX, rp.minY, rp.maxX, rp.maxY\n"
//                + "FROM Resource r JOIN Resource_Place rp ON r.id = rp.id_resource, (SELECT way FROM resource_place Where colum_value = 'Cajazeiras') c\n"
//                + "WHERE ST_Intersects(rp.way, c.way);";

        String sql = "SELECT r.id, r.description, r.format, r.url, r.id_dataset, rp.repeat_number, rp.rows_number, rp.colum_value,\n"
                + "rp.metadata_Created, rp.minX, rp.minY, rp.maxX, rp.maxY, ST_AsEWKT(rp.way) way\n"
                + "FROM Resource r JOIN Resource_Place rp ON r.id = rp.id_resource\n"
                + "WHERE ST_Intersects(rp.way, ?) ORDER BY id";
        try {
            conectar();
            WKTWriter writer = new WKTWriter();
            PreparedStatement pstm = getConnection().prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstm.setString(1,writer.write(placeToSearch.getWay()));

            ResultSet rs = pstm.executeQuery();

            Resource r;

            while (rs.next()) {
                r = formaResource(rs);
//                r.setPrecisionScore(PlaceUtils.getScoreOfAprox(r, placeToSearch, new Float(0.5)));
                resources.add(r);
            }
            
            return resources;
        } catch (URISyntaxException | IOException | SQLException | ClassNotFoundException | ParseException ex) {
            Logger.getLogger(KeyPlaceBdDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resources;
    }

    private Resource formaResource(ResultSet rs) throws SQLException, ParseException {
        String id_resource = rs.getString("id");
        String description = rs.getString("description");
        String format = rs.getString("format");
        String url = rs.getString("url");
        String idDataset = rs.getString("id_Dataset");
        List<KeyPlace> keyplaces = new ArrayList<>();
        
        double maxx,maxy,minx,miny;
        
        maxx = rs.getDouble("maxx");
        maxy = rs.getDouble("maxy");
        minx = rs.getDouble("minx");
        miny = rs.getDouble("miny");
        
       // System.out.println("ID ["+rs.getString("id")+"]");
        
        do{ 
            if(rs.getDouble("maxx") > maxx)
                maxx = rs.getDouble("maxx");
            if(rs.getDouble("maxy") > maxy)
                maxy = rs.getDouble("maxy");
            if(rs.getDouble("minx") < minx)
                minx = rs.getDouble("minx");
            if(rs.getDouble("miny") < miny)
                miny = rs.getDouble("miny");
            
            Place place = new Place();
            KeyPlace keyPlace = new KeyPlace();
            WKTReader reader = new WKTReader();
            
            /*System.out.println("minX: "+rs.getDouble("minx"));
            System.out.println("minY: "+rs.getDouble("miny"));
            System.out.println("maxX: "+rs.getDouble("maxx"));
            System.out.println("maxY: "+rs.getDouble("maxy"));*/
            
            System.out.println("\n\n");
            
            place.setMaxX(rs.getDouble("maxx"));
            place.setMaxY(rs.getDouble("maxy"));
            place.setMinX(rs.getDouble("minx"));
            place.setMinY(rs.getDouble("miny"));
            place.setWay(reader.read(rs.getString("way")));

            keyPlace.setIdResource(id_resource);
            keyPlace.setPlace(place);
            keyPlace.setColumValue(rs.getString("colum_value"));
            keyPlace.setRowsNumber(rs.getInt("rows_Number"));
            keyPlace.setMetadataCreated(rs.getTimestamp("metaData_created"));
            keyPlace.setRepeatNumber(rs.getInt("repeat_number"));
            keyplaces.add(keyPlace);
            
        } while(rs.next() && id_resource.equals(rs.getString("id")));

        rs.previous();
        Resource r = new Resource(id_resource, description, format, url, idDataset);
        Place p = new Place();
        p.setMaxX(maxx);
        p.setMaxY(maxy);
        p.setMinX(minx);
        p.setMinY(miny);
//        p.setWay(new WKTReader().read("POLYGON((" +minx +" "+ miny +", " + maxx +" " + maxy + "))"));
        r.setPlace(p);
        
        for (KeyPlace kp : keyplaces) {
            r.addKeyPlace(kp);
        }
        
        return r;
    }
}