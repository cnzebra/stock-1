/**
 * File Name:    FindTest.java
 * <p/>
 * File Desc:    TODO
 * <p/>
 * Product AB:   TODO
 * <p/>
 * Product Name: TODO
 * <p/>
 * Module Name:  TODO
 * <p/>
 * Module AB:    TODO
 * <p/>
 * Author:       敖海样
 * <p/>
 * History:      2016/2/14 created by hy.ao
 */
package com.ausky.stock.trade;

import com.ausky.stock.log.LogUtil;
import com.ausky.stock.util.DBUtil;
import junit.framework.TestCase;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: 敖海样
 * Date: 2016/2/14
 * Time: 22:43
 * 文件说明：TODO
 */
public class FindTest extends TestCase
{

    public void testTrade() throws Exception
    {
        int volume = 1000;

        String startDate = "20070101";
        String endDate = "20160101";
        String stockCode = "000157";
        String stockMarket = "SZ";

        boolean isBuy = true; // 交易方向
        String tradeDate = Find.findBuy( stockCode, stockMarket, startDate );

        Double buyPrice = getPrice( stockCode, stockMarket, tradeDate );
        double totalMoney = 0;
        int totalTradeTimes = 0;
        int getMoneyTimes = 0;

        while ( tradeDate != null && endDate.compareTo( tradeDate ) > 0 )
        {
            if ( isBuy )
            {
                tradeDate = Find.findSale( stockCode, stockMarket, tradeDate );
                double salePrice = getPrice( stockCode, stockMarket, tradeDate );
                totalMoney += ( getPrice( stockCode, stockMarket, tradeDate ) - buyPrice ) * volume;

                totalTradeTimes++;
                getMoneyTimes += ( salePrice > buyPrice ) ? 1 : 0;
            } else
            {
                tradeDate = Find.findBuy( stockCode, stockMarket, tradeDate );
                buyPrice = getPrice( stockCode, stockMarket, tradeDate );
            }
            isBuy = !isBuy;
        }
        LogUtil.info( "每次交易1000股，累计收益：" + totalMoney + ";收益率:" );
        LogUtil.info( "总共投资次数：" + totalTradeTimes + "盈利次数:" + getMoneyTimes + ",盈亏比例" + ( getMoneyTimes * 1.0 / totalTradeTimes * 100 ) + "%" );
    }

    private Double getPrice( String stockcode, String stockMarket, String startDate ) throws Exception
    {
        try
        {
            String tableName = stockMarket.toUpperCase() + stockcode + "F";
            Connection connection = DBUtil.getConnection2();
            StringBuilder _sql = new StringBuilder( "select tradedate,close from " + tableName + " where tradedate = :tradedate  order by  tradedate asc " );

            PreparedStatement queryStatement = connection.prepareStatement( _sql.toString() );
            queryStatement.setString( 1, startDate );
            ResultSet queryResult = queryStatement.executeQuery();

            if ( queryResult.next() )
            {
                return queryResult.getDouble( "close" );
            }
        } catch ( Exception e )
        {
            e.printStackTrace();
        } finally
        {
            DBUtil.close();
        }

        return null;
    }
}