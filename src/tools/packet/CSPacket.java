package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import handling.SendPacketOpcode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import server.CashCategory;
import server.CashItem;
import server.CashItemFactory;
import server.CashItemInfo;
import server.CashShop;
import tools.HexTool;
import tools.Pair;
import tools.data.MaplePacketLittleEndianWriter;
import static tools.packet.PacketHelper.addExpirationTime;

public class CSPacket {

    private static final int Operation_Code = 2;

    public static byte[] enableCSUse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_USE.getValue());
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] disableCS() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_USE.getValue());
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] warpCS(MapleClient c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPEN.getValue());
        PacketHelper.addCharacterInfo(mplew, c.getPlayer());

        return mplew.getPacket();
    }

    public static byte[] warpCSInfo(MapleClient c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_INFO.getValue());

        CashItemFactory cif = CashItemFactory.getInstance();

        int[] unk_cashSize = new int[]{110100044, 110100045, 110100047,110100054, 110100056, 110100063, 110100075};
        mplew.writeInt(unk_cashSize.length);
        for (int i = 0 ; i < unk_cashSize.length ; i++) {
            mplew.writeInt(unk_cashSize[i]);
        }
        
        
        Collection<CashItemInfo.CashModInfo> ami = cif.getAllModInfo();
        ami.clear();
        mplew.writeShort(ami.size());
        ami.stream().forEach((cmi) -> { // 是控制商城商品用的
            addModCashItemInfo(mplew, cmi);
        });

        mplew.writeShort(0);

        int boxSize = 0;
        mplew.writeInt(boxSize);
        for (int i = 0 ; i < boxSize ; i++) { // 是可使用的箱子還是時尚隨機箱子還是什麼的, 反正跟顯示商品無關
            mplew.writeInt(0); // 隨機箱子道具ID
            int size = 0;
            mplew.writeInt(size); // 隨機箱子可開出的道具的個數
            for (int j = 0 ; j < size ; j++) {
                mplew.writeInt(0); // 隨機箱子可開出的道具的SN?反正不是道具ID
            }
        }

        int unkSize1 = 0;
        mplew.writeInt(unkSize1);
        for (int i = 0 ; i < unkSize1 ; i++) { // 估計是設定選中對應券預覽時設定的顯示券
            mplew.writeInt(i);
            mplew.writeInt(0); // 道具SN[F7 A4 98 00] [F8 A4 98 00]
            mplew.writeInt(0); // 道具ID[96 95 4E 00閃亮髮型卷] [C8 9D 4E 00閃亮整型卷]
            mplew.writeLong(0); // [00 A0 C1 29 E5 82 CE 01]
            mplew.writeLong(0); // [00 80 39 0F 01 93 CE 01]
            mplew.writeInt(0); // [00 00 00 00]
            mplew.writeInt(0); // [05 00 00 00]
            mplew.writeInt(0); // [00 00 00 00]
            mplew.writeInt(0); // [14 00 00 00]
            mplew.writeInt(0); // [1E 00 00 00]
            mplew.writeInt(0); // [28 00 00 00]
            mplew.writeInt(0); // [32 00 00 00]
        }

        List<CashItem> menuItems = cif.getMenuItems();
        mplew.writeInt(menuItems.size());
        for (CashItem menuitem : menuItems) { // 主頁推薦商品
            mplew.write(menuitem.getFlag());
            mplew.writeInt(menuitem.getSN()); // 道具SN[A8 72 F8 08] [04 A3 BD 0A] [2E 0E 27 07]
        }

        mplew.writeInt(0);
        mplew.writeInt(3);
        mplew.write(HexTool.getByteArrayFromHexString("02 00 3F 3F 29 00 3F 3F 3F 20 3F 3F 3F 20 3F 3F 20 3F 3F 3F 3F 20 3F 3F 3F 3F 20 3F 20 3F 3F 3F 20 3F 3F 3F 3F 20 2D 2D 2D 2D 2D 3E 20 3F 3F 05 00 3F 3F 3F 20 3F 13 00 3F 3F 3F 3F 3F 21 20 3F 3F 3F 20 3F 3F 3F 20 3F 3F 3F 21 03 00 3F 3F 3F 20 00 3F 3F 3F 20 3F 3F 3F 3F 20 3F 3F 3F 3F 20 3F 3F 3F 21 20 3F 3F 20 3F 3F 20 3F 3F 3F 3F 3F 3F 7E CC 0B 00 00 64 00 65 00 73 00 63 00 00 00 00 00 55 12 38 12 00 00 00 80 DB 0B 00 00 64 00 65 00 73 00 63 00 00 00 00 00 50 12 38 12 00 00 00 80 EA 0B 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 5F 12 38 12 00 00 00 80 E1 0B 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 5A 12 38 12 00 00 00 80 D8 0B 00 00 64 00 65 00 73 00 63 00 00 00 00 00 59 12 38 12 00 00 00 80 E7 0B 00 00 64 00 65 00 73 00 63 00 00 00 00 00 24 12 38 12 00 00 00 80 F6 0B 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 23 12 38 12 00 00 00 80 ED 0B 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 2E 12 38 12 00 00 00 80 73 0D 00 00 64 00 65 00 73 00 63 00 00 00 00 00 2D 12 38 12 00 00 00 80 F3 0B 00 00 64 00 65 00 73 00 63 00 00 00 00 00 28 12 38 12 00 00 00 80 02 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 37 12 38 12 00 00 00 80 F9 0B 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 32 12 38 12 00 00 00 80 D6 07 00 00 64 00 65 00 73 00 63 00 00 00 00 00 31 12 38 12 00 00 00 80 FF 0B 00 00 64 00 65 00 73 00 63 00 00 00 00 00 3C 12 38 12 00 00 00 80 0E 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 3B 12 38 12 00 00 00 80 05 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 06 12 38 12 00 00 00 80 E2 07 00 00 64 00 65 00 73 00 63 00 00 00 00 00 05 12 38 12 00 00 00 80 0B 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 00 12 38 12 00 00 00 80 1A 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 0F 12 38 12 00 00 00 80 11 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 0A 12 38 12 00 00 00 80 EE 07 00 00 64 00 65 00 73 00 63 00 00 00 00 00 09 12 38 12 00 00 00 80 17 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 14 12 38 12 00 00 00 80 20 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 13 12 38 12 00 00 00 80 29 0C 00 00 0E 5C BD 89 F8 66 00 00 00 00 00 00 1E 12 38 12 00 00 00 80 1D 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 1D 12 38 12 00 00 00 80 FA 07 00 00 64 00 65 00 73 00 63 00 00 00 00 00 18 12 38 12 00 00 00 80 26 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 E7 1D 38 12 00 00 00 80 35 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 E2 1D 38 12 00 00 00 80 2C 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 E1 1D 38 12 00 00 00 80 DE 08 00 00 64 00 65 00 73 00 63 00 00 00 00 00 EC 1D 38 12 00 00 00 80 32 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 EB 1D 38 12 00 00 00 80 41 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 F6 1D 38 12 00 00 00 80 38 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 F5 1D 38 12 00 00 00 80 A2 0E 00 00 64 00 65 00 73 00 63 00 00 00 00 00 F0 1D 38 12 00 00 00 80 3E 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 FF 1D 38 12 00 00 00 80 4D 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 FA 1D 38 12 00 00 00 80 44 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 F9 1D 38 12 00 00 00 80 AE 0E 00 00 64 00 65 00 73 00 63 00 00 00 00 00 C4 1D 38 12 00 00 00 80 4A 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 C3 1D 38 12 00 00 00 80 59 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 CE 1D 38 12 00 00 00 80 50 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 CD 1D 38 12 00 00 00 80 4A 09 00 00 64 00 65 00 73 00 63 00 00 00 00 00 C8 1D 38 12 00 00 00 80 56 0C 00 00 64 00 65 00 73 00 63 00 00 00 00 00 D7 1D 38 12 00 00 00 80 62 0C 00 00 6E 00 61 00 6D 00 65 00 00 00 00 00 D2 1D 38 12 00 00 00 80 6C 0B 00 00 37 00 31 90 74 5E E5 65 18 8A 00 00 D1 1D 38 12 00 00 00 80 00 00 01 00 81 3D 4D 00 51 FE 8F 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF 0E 00 00 00 7B 9E 33 01 7B 9E 33 01 00 00 00 00 18 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 01 00 00 00 00 E1 CB 57 00 00 00 00 3C 00 00 00 00 00 00 00 02 00 00 00 01 FA AE 4F 00 2B 00 15 4D 4C 00 16 4D 4C 00 17 4D 4C 00 1D 4C 4C 00 7F 4C 4C 00 41 4B 4C 00 42 4B 4C 00 43 4B 4C 00 44 4B 4C 00 45 4B 4C 00 48 4B 4C 00 49 4B 4C 00 4A 4B 4C 00 4B 4B 4C 00 4D 4B 4C 00 50 4B 4C 00 54 4B 4C 00 56 4B 4C 00 57 4B 4C 00 58 4B 4C 00 5B 4B 4C 00 6B 4B 4C 00 6E 4B 4C 00 82 4B 4C 00 88 4B 4C 00 91 4B 4C 00 0B 4C 4C 00 0C 4C 4C 00 0D 4C 4C 00 7D 4C 4C 00 80 4C 4C 00 81 4C 4C 00 82 4C 4C 00 96 4C 4C 00 97 4C 4C 00 98 4C 4C 00 9F 4B 4C 00 F6 4B 4C 00 F7 4B 4C 00 F8 4B 4C 00 53 4C 4C 00 54 4C 4C 00 55 4C 4C 00 01 EB AE 4F 00 29 00 5D 11 10 00 EB 5F 10 00 0F FB 19 00 E7 72 4C 00 FE D3 10 00 2D 54 0F 00 DF FA 19 00 26 73 0F 00 C9 53 0F 00 CA 53 0F 00 24 99 0F 00 25 73 0F 00 82 F8 10 00 7F 03 11 00 83 F8 10 00 80 03 11 00 CA 4A 0F 00 59 71 0F 00 60 98 0F 00 D7 D0 10 00 F6 82 10 00 19 5C 10 00 58 BF 0F 00 5A 95 4E 00 5B 95 4E 00 31 9D 4E 00 32 9D 4E 00 F5 A0 4E 00 39 99 4E 00 64 9D 4E 00 65 9D 4E 00 66 9D 4E 00 67 9D 4E 00 68 9D 4E 00 69 9D 4E 00 6A 9D 4E 00 6B 9D 4E 00 82 3A 54 00 04 3F 4D 00 F1 7B 4D 00 80 64 4D 00 00 00 00 00 00"));
        
        return mplew.getPacket();
    }
        
    public static void addModCashItemInfo(MaplePacketLittleEndianWriter mplew, CashItemInfo.CashModInfo item) {
        int flags = item.flags;
        mplew.writeInt(item.sn); // 商品SN
        mplew.writeInt(flags); // 商品FLAG
        mplew.writeInt(0); // Unk
        if ((flags & 0x1) != 0) {
            mplew.writeInt(item.itemid);
        }
        if ((flags & 0x2) != 0) {
            mplew.writeShort(item.count);
        }
        if ((flags & 0x10) != 0) {
            mplew.write(item.priority);
        }
        if ((flags & 0x4) != 0) {
            mplew.writeInt(item.discountPrice);
        }
        if ((flags & 0x8) != 0) {
            mplew.write(item.unk_1 - 1);
        }
        if ((flags & 0x20) != 0) {
            mplew.writeShort(item.period);
        }
        if ((flags & 0x20000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x40000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x40) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 0x80) != 0) {
            mplew.writeInt(item.meso);
        }
        if ((flags & 0x100) != 0) {
            mplew.write(item.unk_2 - 1);
        }
        if ((flags & 0x200) != 0) {
            mplew.write(item.gender);
        }
        if ((flags & 0x400) != 0) {
            mplew.write(item.showUp ? 1 : 0);
        }
        if ((flags & 0x800) != 0) {
            mplew.write(item.mark);
        }
        if ((flags & 0x1000) != 0) {
            mplew.write(item.unk_3 - 1);
        }
        if ((flags & 0x2000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x4000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x8000) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 0x10000) != 0) {
            List pack = CashItemFactory.getInstance().getPackageItems(item.sn);
            if (pack == null) {
                mplew.write(0);
            } else {
                mplew.write(pack.size());
                pack.stream().forEach((pack1) -> {
                    mplew.writeInt((Integer) pack1);
                });
            }
        }
        
        if ((flags & 0x80000) != 0) { // 開始販售時間[完成]
            mplew.writeInt(2016020300); // 2016020300
        }
        
        if ((flags & 0x100000) != 0) { // 結束販售時間[完成]
            mplew.writeInt(2016020300); // 2016020324
        }
        
        if ((flags & 0x4000000) != 0) { // [完成]
            mplew.write(0); 
        }        
        
        if (((flags & 0x80000) == 0) || (((flags & 0x100000) == 0)
                || ((flags & 0x200000) != 0))) {
            mplew.write(0);
        }
    }

    public static byte[] changeCategory(int subcategory) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        mplew.write(11);
        CashItemFactory cif = CashItemFactory.getInstance();
        mplew.write(cif.getCategoryItems(subcategory).size() > 0 ? 1 : 3);
        mplew.write(cif.getCategoryItems(subcategory).size());
        for (CashItem i : cif.getCategoryItems(subcategory)) {
            addCSItemInfo(mplew, i);
        }

        return mplew.getPacket();
    }

    public static byte[] showNXChar(int subcategory) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        mplew.write(19);
        mplew.write(1);
        mplew.write(HexTool.getByteArrayFromHexString("24 80 8D 5B 00 90 B4 5B 00 6A 2D 10 00 00 00 68 31 31 01 7B 4F 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 06 00 00 00 8C 0A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 8C 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 D7 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 52 61 6C 70 68 69 65 80 0C 16 60 B0 FF E7 7F FE 47 25 B0 83 F3 F9 3F FF C3 FF F0 09 03 00 07 00 00 00 00 80 8D 5B 00 90 B4 5B 00 26 30 10 00 00 00 9F CE 38 01 AB 34 10 00 01 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 60 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 60 09 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 43 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 52 61 6C 70 68 69 65 80 0C 16 60 B0 FF E7 7F FE 47 25 B0 83 F3 F9 3F FF C3 FF F0 09 03 00 07 00 00 00 00 80 8D 5B 00 90 B4 5B 00 04 2D 10 00 00 00 67 62 3D 01 BF F8 19 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 94 11 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 14 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 52 61 6C 70 68 69 65 80 0C 16 60 B0 FF E7 7F FE 47 25 B0 83 F3 F9 3F FF C3 FF F0 09 03 00 07 00 00 00 00 80 8D 5B 00 A0 DB 5B 00 30 2E 10 00 00 00 48 3A 34 01 40 98 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 0D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 7A 6F 6D 62 69 65 6B 69 6C 6C 65 72 C5 2E 40 FC 7F FE 07 02 FF 07 04 F2 80 6F F8 3F 9F 78 96 72 25 31 00 07 00 00 00 00 80 8D 5B 00 A0 DB 5B 00 C1 2F 10 00 00 00 A8 47 37 01 60 E6 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 AC 0D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AC 0D 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 7A 6F 6D 62 69 65 6B 69 6C 6C 65 72 C5 2E 40 FC 7F FE 07 02 FF 07 04 F2 80 6F F8 3F 9F 78 96 72 25 31 00 07 00 00 00 00 80 8D 5B 00 A0 DB 5B 00 26 30 10 00 00 00 3B CE 38 01 7F 34 10 00 01 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 34 08 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 34 08 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 42 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 7A 6F 6D 62 69 65 6B 69 6C 6C 65 72 C5 2E 40 FC 7F FE 07 02 FF 07 04 F2 80 6F F8 3F 9F 78 96 72 25 31 00 07 00 00 00 00 80 8D 5B 00 A0 DB 5B 00 88 30 10 00 00 00 C4 54 3A 01 EF 5B 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 8C 0A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 8C 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 7A 6F 6D 62 69 65 6B 69 6C 6C 65 72 C5 2E 40 FC 7F FE 07 02 FF 07 04 F2 80 6F F8 3F 9F 78 96 72 25 31 00 07 00 00 00 00 80 8D 5B 00 A0 DB 5B 00 04 2D 10 00 00 00 59 64 3D 01 9B F9 19 00 01 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1B 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 7A 6F 6D 62 69 65 6B 69 6C 6C 65 72 C5 2E 40 FC 7F FE 07 02 FF 07 04 F2 80 6F F8 3F 9F 78 96 72 25 31 00 07 00 00 00 00 80 8D 5B 00 A0 DB 5B 00 B4 31 10 00 00 00 24 F6 41 01 4F D1 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 74 0E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 74 0E 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 7A 6F 6D 62 69 65 6B 69 6C 6C 65 72 C5 2E 40 FC 7F FE 07 02 FF 07 04 F2 80 6F F8 3F 9F 78 96 72 25 31 00 07 00 00 00 00 80 8D 5B 00 B0 02 5C 00 5C 2F 10 00 00 00 07 C1 35 01 7D 0D 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 EC 13 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 EC 13 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 42 72 75 63 65 80 00 FD EC 7F FE E7 7F FE 67 07 F2 08 C8 F8 3F FF C3 FF B0 24 07 00 07 00 00 00 00 80 8D 5B 00 B0 02 5C 00 88 30 10 00 00 00 CE 54 3A 01 48 5C 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 08 07 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 09 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 42 72 75 63 65 80 00 FD EC 7F FE E7 7F FE 67 07 F2 08 C8 F8 3F FF C3 FF B0 24 07 00 07 00 00 00 00 80 8D 5B 00 B0 02 5C 00 04 2D 10 00 00 00 5D 64 3D 01 95 F9 19 00 01 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 42 72 75 63 65 80 00 FD EC 7F FE E7 7F FE 67 07 F2 08 C8 F8 3F FF C3 FF B0 24 07 00 07 00 00 00 00 80 8D 5B 00 C0 29 5C 00 6E 2D 10 00 00 00 99 2D 31 01 33 4B 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 3C 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 3C 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 43 6C 69 66 66 42 02 32 60 24 FF 67 0A FF E7 0A 02 09 3A F8 3F FF C3 FF 50 05 07 00 07 00 00 00 00 80 8D 5B 00 C0 29 5C 00 30 2E 10 00 00 00 9A 3A 34 01 83 98 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 B8 0B 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 B8 0B 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 43 6C 69 66 66 42 02 32 60 24 FF 67 0A FF E7 0A 02 09 3A F8 3F FF C3 FF 50 05 07 00 07 00 00 00 00 80 8D 5B 00 C0 29 5C 00 5C 2F 10 00 00 00 5D C1 35 01 8B 0D 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 94 11 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 43 6C 69 66 66 42 02 32 60 24 FF 67 0A FF E7 0A 02 09 3A F8 3F FF C3 FF 50 05 07 00 07 00 00 00 00 80 8D 5B 00 C0 29 5C 00 88 30 10 00 00 00 F9 54 3A 01 BA 5B 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 34 08 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 34 08 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 3E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 43 6C 69 66 66 42 02 32 60 24 FF 67 0A FF E7 0A 02 09 3A F8 3F FF C3 FF 50 05 07 00 07 00 00 00 00 80 8D 5B 00 C0 29 5C 00 04 2D 10 00 00 00 42 62 3D 01 9A F8 19 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 CC 10 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 CC 10 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 43 6C 69 66 66 42 02 32 60 24 FF 67 0A FF E7 0A 02 09 3A F8 3F FF C3 FF 50 05 07 00 07 00 00 00 00 80 8D 5B 00 D0 50 5C 00 CC 2D 10 00 00 00 A4 B3 32 01 3C 71 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 60 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 60 09 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 3A 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 00 73 74 72 69 65 6E 03 01 E5 E6 7F 38 70 06 FF E7 54 42 4A 26 F8 3F FF C3 FF 30 1D 03 00 07 00 00 00 00 80 8D 5B 00 D0 50 5C 00 30 2E 10 00 00 00 F4 3A 34 01 63 98 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 11 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 00 73 74 72 69 65 6E 03 01 E5 E6 7F 38 70 06 FF E7 54 42 4A 26 F8 3F FF C3 FF 30 1D 03 00 07 00 00 00 00 80 8D 5B 00 D0 50 5C 00 5C 2F 10 00 00 00 57 C3 35 01 B3 0E 10 00 01 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 00 73 74 72 69 65 6E 03 01 E5 E6 7F 38 70 06 FF E7 54 42 4A 26 F8 3F FF C3 FF 30 1D 03 00 07 00 00 00 00 80 8D 5B 00 D0 50 5C 00 04 2D 10 00 00 00 1D 63 3D 01 59 F9 19 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 42 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 00 73 74 72 69 65 6E 03 01 E5 E6 7F 38 70 06 FF E7 54 42 4A 26 F8 3F FF C3 FF 30 1D 03 00 07 00 00 00 00 80 8D 5B 00 E0 77 5C 00 6B 2D 10 00 00 00 98 2F 31 01 4C 4E 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 80 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 80 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 6A 61 63 6B 6F 68 65 61 72 74 73 46 02 4C 8D 8A 69 F1 7F FE 67 1C D0 08 F5 F9 3F FF C3 FF 10 1B 05 00 07 00 00 00 00 80 8D 5B 00 E0 77 5C 00 CC 2D 10 00 00 00 18 B4 32 01 D4 71 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 60 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 60 09 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 14 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 6A 61 63 6B 6F 68 65 61 72 74 73 46 02 4C 8D 8A 69 F1 7F FE 67 1C D0 08 F5 F9 3F FF C3 FF 10 1B 05 00 07 00 00 00 00 80 8D 5B 00 E0 77 5C 00 5C 2F 10 00 00 00 89 C1 35 01 01 06 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 04 10 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 04 10 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 13 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 6A 61 63 6B 6F 68 65 61 72 74 73 46 02 4C 8D 8A 69 F1 7F FE 67 1C D0 08 F5 F9 3F FF C3 FF 10 1B 05 00 07 00 00 00 00 80 8D 5B 00 E0 77 5C 00 04 2D 10 00 00 00 6A 64 3D 01 48 F9 19 00 01 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 94 11 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 12 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 6A 61 63 6B 6F 68 65 61 72 74 73 46 02 4C 8D 8A 69 F1 7F FE 67 1C D0 08 F5 F9 3F FF C3 FF 10 1B 05 00 07 00 00 00 00 80 8D 5B 00 F0 9E 5C 00 5C 2F 10 00 00 00 55 C3 35 01 B2 0E 10 00 01 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 1C 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 1C 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 2B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 62 61 62 79 64 30 31 31 79 44 71 F0 7F FE E7 7F FE A7 54 F2 3F 02 FA 3F 6F C8 FF F0 25 43 00 07 00 00 00 00 80 8D 5B 00 F0 9E 5C 00 88 30 10 00 00 00 60 56 3A 01 82 5D 10 00 01 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 08 07 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 31 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 62 61 62 79 64 30 31 31 79 44 71 F0 7F FE E7 7F FE A7 54 F2 3F 02 FA 3F 6F C8 FF F0 25 43 00 07 00 00 00 00 80 8D 5B 00 F0 9E 5C 00 04 2D 10 00 00 00 5C 64 3D 01 9F F9 19 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 22 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 62 61 62 79 64 30 31 31 79 44 71 F0 7F FE E7 7F FE A7 54 F2 3F 02 FA 3F 6F C8 FF F0 25 43 00 07 00 00 00 00 80 8D 5B 00 F0 9E 5C 00 B4 31 10 00 00 00 07 F6 41 01 1F D1 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 1C 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 1C 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 17 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 62 61 62 79 64 30 31 31 79 44 71 F0 7F FE E7 7F FE A7 54 F2 3F 02 FA 3F 6F C8 FF F0 25 43 00 07 00 00 00 00 80 8D 5B 00 00 C6 5C 00 E0 32 10 00 00 00 2B 2D 31 01 CA 4A 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 06 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 08 07 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 A5 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 4D 65 72 6B 79 79 45 A5 51 17 FF E7 7F FE 27 4A F2 3F 5E 4A 98 FF F3 01 D2 16 67 00 07 00 00 00 00 80 8D 5B 00 00 C6 5C 00 5C 2F 10 00 00 00 36 C3 35 01 88 0E 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 D8 0E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 D8 0E 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 12 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 4D 65 72 6B 79 79 45 A5 51 17 FF E7 7F FE 27 4A F2 3F 5E 4A 98 FF F3 01 D2 16 67 00 07 00 00 00 00 80 8D 5B 00 00 C6 5C 00 04 2D 10 00 00 00 D4 62 3D 01 26 F9 19 00 01 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 50 14 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 50 14 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 25 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 4D 65 72 6B 79 79 45 A5 51 17 FF E7 7F FE 27 4A F2 3F 5E 4A 98 FF F3 01 D2 16 67 00 07 00 00 00 00 80 8D 5B 00 10 ED 5C 00 6B 2D 10 00 00 00 64 2E 31 01 AB 4C 0F 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 80 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 80 0C 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 61 64 61 76 69 61 47 D3 70 53 FF E7 7F FE 67 1D F1 3F 01 F9 3F FF 33 7D 32 0A 0F 00 07 00 00 00 00 80 8D 5B 00 10 ED 5C 00 5C 2F 10 00 00 00 60 C1 35 01 ED 09 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A0 0F 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 0C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 61 64 61 76 69 61 47 D3 70 53 FF E7 7F FE 67 1D F1 3F 01 F9 3F FF 33 7D 32 0A 0F 00 07 00 00 00 00 80 8D 5B 00 10 ED 5C 00 88 30 10 00 00 00 12 55 3A 01 81 5C 10 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 B0 04 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 B0 04 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 3B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 61 64 61 76 69 61 47 D3 70 53 FF E7 7F FE 67 1D F1 3F 01 F9 3F FF 33 7D 32 0A 0F 00 07 00 00 00 00 80 8D 5B 00 10 ED 5C 00 04 2D 10 00 00 00 66 62 3D 01 C1 F8 19 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 94 11 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 61 64 61 76 69 61 47 D3 70 53 FF E7 7F FE 67 1D F1 3F 01 F9 3F FF 33 7D 32 0A 0F 00 07 00 00 00 00"));

        return mplew.getPacket();
    }

    public static byte[] addFavorite(int itemSn) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        mplew.write(14);//16 remove
        mplew.write(1);
        mplew.writeInt(itemSn);

        return mplew.getPacket();
    }

    public static byte[] showCSAccount(MapleClient c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_ACCOUNT.getValue());
        mplew.writeBoolean(true);
        mplew.writeMapleAsciiString(c.getAccountName());

        return mplew.getPacket();
    }

    public static byte[] Like(int item) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        mplew.write(15);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] Favorite(MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_SHOP_UPDATE.getValue());
        mplew.write(18);
        mplew.write(chr.getWishlistSize() > 0 ? 1 : 3);
        mplew.write(chr.getWishlistSize());
        CashItemFactory cif = CashItemFactory.getInstance();
        mplew.write(chr.getWishlistSize() > 0 ? 1 : 3);
        mplew.write(chr.getWishlistSize());
        for (int i : chr.getWishlist()) {
            CashItem ci = cif.getAllItem(i);
//        for (CashItem i : cif.getMenuItems(301)) {
            addCSItemInfo(mplew, ci);
        }
        return mplew.getPacket();
    }

    public static void addCSItemInfo(MaplePacketLittleEndianWriter mplew, CashItem item) {
        mplew.writeInt(item.getCategory());
        mplew.writeInt(item.getSubCategory()); //4000000 + 10000 + page * 10000
        mplew.writeInt(item.getParent()); //1000000 + 70000 + page * 100 + item on page
        mplew.writeMapleAsciiString(item.getImage()); //jpeg img url
        mplew.writeInt(item.getSN());
        mplew.writeInt(item.getItemId());
        mplew.writeInt(1);
        mplew.writeInt(item.getFlag());//1 =event 2=new = 4=hot
        mplew.writeInt(0);//1 = package?
        mplew.writeInt(0);//changes - type?
        mplew.writeInt(item.getPrice());
        mplew.write(HexTool.getByteArrayFromHexString("00 80 22 D6 94 EF C4 01")); // 1/1/2005
        mplew.writeLong(PacketHelper.MAX_TIME);
        mplew.write(HexTool.getByteArrayFromHexString("00 80 22 D6 94 EF C4 01")); // 1/1/2005
        mplew.writeLong(PacketHelper.MAX_TIME);
        mplew.writeInt(item.getPrice()); //after discount
        mplew.writeInt(0);
        mplew.writeInt(item.getQuantity());
        mplew.writeInt(item.getExpire());
        mplew.write(1); //buy
        mplew.write(1); //gift
        mplew.write(1); //cart
        mplew.write(0);
        mplew.write(1); //favorite
        mplew.writeInt(item.getGender());//gender female 1 male 0 nogender 2
        mplew.writeInt(item.getLikes()); //likes
        mplew.writeInt(0);
//        if(ispack){
//            mplew.writeAsciiString("lol");
//            mplew.writeShort(0);
//        }else{
        mplew.writeInt(0);
//        }
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0); // v146.1
        mplew.write(0); // v146.1
        List pack = CashItemFactory.getInstance().getPackageItems(item.getSN());
        if (pack == null) {
            mplew.writeInt(0);
        } else {
            mplew.writeInt(pack.size());
            for (int i = 0; i < pack.size(); i++) {
                mplew.writeInt(100000677);//item.getSN()); //should be pack item sn
                mplew.writeInt(1072443);//((Integer) pack.get(i)).intValue());
                mplew.writeInt(1);//1
                mplew.writeInt(3600); //pack item usual price
                mplew.writeInt(2880); //pack item discounted price
                mplew.writeInt(0);
                mplew.writeInt(1);
                mplew.writeInt(0);
                mplew.writeInt(2);
            }
        }
    }

    public static byte[] showNXMapleTokens(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_UPDATE.getValue());
        mplew.writeInt(chr.getCSPoints(1)); // 樂豆點數
        mplew.writeInt(chr.getCSPoints(2)); // 楓葉點數
        mplew.writeInt(chr.getCSPoints(3)); // 里程點數

        return mplew.getPacket();
    }

    public static byte[] CashUse(int id, int a, int b, int startDate, int endDate) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        //1F 04 00 00 00 00 A2 37 A0 00 32 00 00 00 D2 00 00 00 00 00 00 00 10 2A 33 01 16 2A 33 01
        mplew.writeShort(SendPacketOpcode.CASH_USE.getValue());
        mplew.writeInt(0);
        mplew.writeInt(id);
        mplew.writeInt(a);
        mplew.writeInt(b);
        mplew.writeInt(0);
        mplew.writeInt(startDate);
        mplew.writeInt(endDate);

        return mplew.getPacket();
    }

    public static byte[] CashUse2(int a) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_USE2.getValue());
        mplew.write(a);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] CashUse3() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_USE3.getValue());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] CashUse4() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASH_USE4.getValue());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] showMesos(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_MESO_UPDATE.getValue());
        mplew.writeZeroBytes(2);
        mplew.write(4);
        mplew.writeZeroBytes(5);
        mplew.writeLong(chr.getMeso());

        return mplew.getPacket();
    }

    public static byte[] LimitGoodsCountChanged() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code);
        mplew.writeInt(0); // SN
        mplew.writeInt(0); // Count
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getCSInventory(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 4); // 5 = Failed + transfer
        CashShop mci = c.getPlayer().getCashInventory();
        mplew.write(0); // Boolean [true => + [Int]]
        mplew.writeShort(mci.getItemsSize());
        if (mci.getItemsSize() > 0) {
            int size = 0;
            CashItemFactory cif = CashItemFactory.getInstance();
            for (Item itemz : mci.getInventory()) {
                addCashItemInfo(mplew, itemz, c.getAccID(), cif.getItemSN(itemz.getItemId()), false);
                if (ItemConstants.類型.寵物(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    size++;
                }
            }
            mplew.writeInt(size);
            for (Item itemz : mci.getInventory()) {
                if (ItemConstants.類型.寵物(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(mplew, itemz);
                }
            }
        }
        mplew.writeShort(c.getPlayer().getStorage().getSlots());
        mplew.writeShort(c.getCharacterSlots());
        mplew.writeShort(0);
        mplew.writeShort(1);

        return mplew.getPacket();
    }

    public static byte[] getCSGifts(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 7); // 7 = Failed + transfer//was8
        List<Pair<Item, String>> mci = c.getPlayer().getCashInventory().loadGifts();
        mplew.writeShort(mci.size());
        for (Pair<Item, String> mcz : mci) { // 70 Bytes, need to recheck.
            mplew.writeLong(mcz.getLeft().getUniqueId());
            mplew.writeInt(mcz.getLeft().getItemId());
            mplew.writeAsciiString(mcz.getLeft().getGiftFrom(), 15);
            mplew.writeAsciiString(mcz.getRight(), 74);
        }

        return mplew.getPacket();
    }

    public static byte[] doCSMagic() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(7); // 7 = Failed + transfer//6
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] sendWishList(MapleCharacter chr, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (update ? 11 : 9)); // 9 = Failed + transfer, 16 = Failed.
        int[] list = chr.getWishlist();
        for (int i = 0; i < 12; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
        return mplew.getPacket();
    }

    public static byte[] showBoughtCSItem(Item item, int sn, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 13);
        addCashItemInfo(mplew, item, accid, sn);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSItem(int itemid, int sn, int uniqueid, int accid, int quantity, String giftFrom, long expire) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 13);
        addCashItemInfo(mplew, uniqueid, accid, itemid, sn, quantity, giftFrom, expire);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSItemFailed(final int mode, final int sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 14);
        mplew.write(mode); // 0/1/2 = transfer, Rest = code
        if (mode == 28 || mode == 29) { // Limit Goods update. this item is out of stock, and therefore not available for sale.
            mplew.writeInt(sn);
        } else if (mode == 48) { // You cannot make any more purchases in %d.\r\nPlease try again in (%d + 1).
            mplew.write(1);	// Hour?	
        } else if (mode == 64) { // %s can only be purchased once a month.
            mplew.writeInt(sn);
            mplew.writeLong(System.currentTimeMillis());
        }

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSPackage(Map<Integer, Item> ccc, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 73);
        mplew.write(ccc.size());
//        int size = 0;
        for (Entry<Integer, Item> sn : ccc.entrySet()) {
            addCashItemInfo(mplew, sn.getValue(), accid, sn.getKey());
//            if (ItemConstants.is寵物(sn.getValue().getItemId()) || GameConstants.getInventoryType(sn.getValue().getItemId()) == MapleInventoryType.EQUIP) {
//                size++;
//            }
        }
//        if (ccc.size() > 0) {
//            mplew.writeInt(size);
//            for (Item itemz : ccc.values()) {
//                if (ItemConstants.is寵物(itemz.getItemId()) || GameConstants.getInventoryType(itemz.getItemId()) == MapleInventoryType.EQUIP) {
//                    PacketHelper.addItemInfo(mplew, itemz);
//                }
//            }
//        }
        mplew.writeShort(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] sendGift(int price, int itemid, int quantity, String receiver, boolean packages) {
        // [ %s ] \r\nwas sent to %s. \r\n%d NX Prepaid \r\nwere spent in the process.
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (packages ? 75 : 20)); // 74 = Similar structure to showBoughtCSItemFailed
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);
        if (packages) {
            mplew.writeShort(0); //maplePoints
        }
//        mplew.writeInt(price);

        return mplew.getPacket();
    }

    public static byte[] showCouponRedeemedItem(Map<Integer, Item> items, int mesos, int maplePoints, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 15);
        mplew.write(items.size());
        for (Entry<Integer, Item> item : items.entrySet()) {
            addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey());
        }
        mplew.writeInt(maplePoints);
        mplew.writeInt(0); // Normal items size
        //for (Pair<Integer, Integer> item : items2) {
        //    mplew.writeInt(item.getRight()); // Count
        //    mplew.writeInt(item.getLeft());  // Item ID
        //}
        mplew.writeInt(mesos);

        return mplew.getPacket();
    }

    public static byte[] showCouponGifted(Map<Integer, Item> items, String receiver, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 17); // 22 = Failed. [Mode - 0/2 = transfer, 15 = invalid 3 times]
        mplew.writeMapleAsciiString(receiver); // Split by ;
        mplew.write(items.size());
        for (Entry<Integer, Item> item : items.entrySet()) {
            addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey());
        }
        mplew.writeInt(0); // (amount of receiver - 1)

        return mplew.getPacket();
    }

    public static byte[] increasedInvSlots(int inv, int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 22);
        mplew.write(inv);
        mplew.writeShort(slots);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] increasedStorageSlots(int slots, boolean characterSlots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (characterSlots ? 26 : 24)); // 32 = Buy Character. O.O
        mplew.writeShort(slots);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] increasedPendantSlots() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 30); // 35 = Failed
        mplew.writeShort(0); // 0 = Add, 1 = Extend
        mplew.writeShort(100); // Related to time->Low/High fileTime
        // The time limit for the %s slot \r\nhas been extended to %d-%d-%d %d:%d.
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] confirmFromCSInventory(Item item, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 32); // 37 = Failed//36 8A
        mplew.write(item.getQuantity());
        mplew.writeShort(pos);
        PacketHelper.addItemInfo(mplew, item);
        mplew.writeInt(0); // For each: 8 bytes(Could be 2 ints or 1 long)
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static byte[] confirmToCSInventory(Item item, int accId, int sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 34);
        addCashItemInfo(mplew, item, accId, sn, false);

        return mplew.getPacket();
    }

    public static byte[] cashItemDelete(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 36); // 41 = Failed //42 is delete
        mplew.writeLong(uniqueid); // or SN?

        return mplew.getPacket();
    }

    public static byte[] rebateCashItem() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 67); // 41 = Failed
        mplew.writeLong(0); // UniqueID
        mplew.writeInt(0); // MaplePoints accumulated
        mplew.writeInt(0); // For each: 8 bytes.

        return mplew.getPacket();
    }

    public static byte[] sendBoughtRings(boolean couple, Item item, int sn, int accid, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + (couple ? 71 : 81));
        addCashItemInfo(mplew, item, accid, sn);
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(item.getItemId());
        mplew.writeShort(1); // Count
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] receiveFreeCSItem(Item item, int sn, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 167); // 105 = Buy Name Change, 107 = Transfer world
        addCashItemInfo(mplew, item, accid, sn);

        return mplew.getPacket();
    }

    public static byte[] cashItemExpired(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 38);
        mplew.writeLong(uniqueid);

        return mplew.getPacket();
    }

    public static byte[] showBoughtCSQuestItem(int price, short quantity, byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 77); // 76 = Failed.
        mplew.writeInt(1); // size. below gets repeated for each.
        mplew.writeInt(quantity);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] updatePurchaseRecord() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 94); // 95 = Failed. //94
        mplew.writeInt(0);
        mplew.write(1); // boolean

        return mplew.getPacket();
    }

    public static byte[] sendCashRefund(final int cash) {
        // Your refund has been processed. \r\n(%d NX Refund)
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 97);
        mplew.writeInt(0); // Item Size.->For each 8 bytes.
        mplew.writeInt(cash); // NX

        return mplew.getPacket();
    }

    public static byte[] sendRandomBox(int uniqueid, Item item, short pos) { // have to revise this
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 99); // 100 = Failed
        mplew.writeLong(uniqueid);
        mplew.writeInt(1302000);
        PacketHelper.addItemInfo(mplew, item);
        mplew.writeShort(0);
        mplew.writeInt(0); // Item Size.->For each 8 bytes.

        return mplew.getPacket();
    }

    public static byte[] sendCashGachapon(final boolean cashItem, int idFirst, Item item, int accid) { // Xmas Surprise, Cash Shop Surprise
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 109); // 110 = Failed.		
        mplew.writeLong(idFirst); //uniqueid of the xmas surprise itself
        mplew.writeInt(0);
        mplew.write(cashItem ? 1 : 0);
        if (cashItem) {
            addCashItemInfo(mplew, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        }
        mplew.writeInt(item.getItemId());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] sendTwinDragonEgg(boolean test1, boolean test2, int idFirst, Item item, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 111); // 112 = Failed.		
        mplew.write(test1 ? 1 : 0);
        mplew.write(test2 ? 1 : 0);
        mplew.writeInt(1);
        mplew.writeInt(2);
        mplew.writeInt(3);
        mplew.writeInt(4);
        if (test1 && test2) {
            addCashItemInfo(mplew, item, accid, 0); //info of the new item, but packet shows 0 for sn?
        }

        return mplew.getPacket();
    }

    public static byte[] sendBoughtMaplePoints(final int maplePoints) {
        // You've received %d Maple Points.
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 113);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(maplePoints);

        return mplew.getPacket();
    }

    public static byte[] changeNameCheck(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANGE_NAME_CHECK.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] changeNameResponse(final int mode, final int pic) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 0: Success
        // 1: The name change is already submitted \r\ndue to the item purchase
        // 2: This applies to the limitations on the request.\r\nPlease check if you were recently banned \r\nwithin 3 months.
        // 3: This applies to the limitations on the request.\r\nPlease check if you requested \r\nfor the name change within a month.
        // default: An unknown error has occured.
        mplew.writeShort(SendPacketOpcode.CHANGE_NAME_RESPONSE.getValue());
        mplew.writeInt(0);
        mplew.write(mode);
        mplew.writeInt(pic); // pic or birthdate

        return mplew.getPacket();
    }

    public static byte[] receiveGachaStamps(final boolean invfull, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GACHAPON_STAMPS.getValue());
        mplew.write(invfull ? 0 : 1);
        if (!invfull) {
            mplew.writeInt(amount);
        }

        return mplew.getPacket();
    }

    public static byte[] freeCashItem(final int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FREE_CASH_ITEM.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] showXmasSurprise(boolean full, int idFirst, Item item, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.XMAS_SURPRISE.getValue());
        mplew.write(full ? 212 : 213);
        if (!full) {
            mplew.writeLong(idFirst); //uniqueid of the xmas surprise itself
            mplew.writeInt(0);
            addCashItemInfo(mplew, item, accid, 0); //info of the new item, but packet shows 0 for sn?
            mplew.writeInt(item.getItemId());
            mplew.write(1);
            mplew.write(1);
        }

        return mplew.getPacket();
    }

    public static byte[] showOneADayInfo(boolean show, int sn) { // hmmph->Buy regular item causes invalid pointer
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ONE_A_DAY.getValue());
        mplew.writeInt(100); //idk-related to main page
        mplew.writeInt(100000); // idk-related to main page
        mplew.writeInt(1); // size of items to buy, for each, repeat 3 ints below.
        mplew.writeInt(20121231); // yyyy-mm-dd
        mplew.writeInt(sn);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] playCashSong(int itemid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CASH_SONG.getValue());
        mplew.writeInt(itemid);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static byte[] useAlienSocket(boolean start) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ALIEN_SOCKET_CREATOR.getValue());
        mplew.write(start ? 0 : 2);

        return mplew.getPacket();
    }

    public static byte[] ViciousHammer(boolean start, int hammered) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());
        mplew.write(start ? 0x42 : 0x46);
        mplew.writeInt(0);
        if (start) {
            mplew.writeInt(hammered);
        }

        return mplew.getPacket();
    }

    public static byte[] getLogoutGift() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGOUT_GIFT.getValue());

        return mplew.getPacket();
    }

    public static byte[] GoldenHammer(byte mode, int success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.VICIOUS_HAMMER.getValue());
        mplew.write(mode);
        mplew.writeInt(success);
        /*
         * success = 1:
         * mode:
         * 3 - 2 upgrade increases\r\nhave been used already.
         */
        return mplew.getPacket();
    }

    public static byte[] PlatinumHammer(int op) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLATINUM_HAMMER.getValue());
        mplew.write(op);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] changePetFlag(int uniqueId, boolean added, int flagAdded) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_FLAG_CHANGE.getValue());
        mplew.writeLong(uniqueId);
        mplew.write(added ? 1 : 0);
        mplew.writeShort(flagAdded);

        return mplew.getPacket();
    }

    public static byte[] changePetName(MapleCharacter chr, String newname, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_NAMECHANGE.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(0);
        mplew.writeMapleAsciiString(newname);
        mplew.write(slot);

        return mplew.getPacket();
    }

    public static byte[] OnMemoResult(final byte act, final byte mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /* 類型(act)：
         * [5] 訊息傳送成功！
         * [6][1] 請確認接收的角色名稱。
         * [6][2] 對方的收信匣已滿！請下次再試。
         * [6][3] 1分最多可傳3個。\r\n請稍後再試。
         * [6][4] 超過一天最多傳送量20個。
         * [6][5] 無法將訊息傳送給自己。
         */
        mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
        mplew.write(act);
        if (act == 6) {
            mplew.write(mode);
        }

        return mplew.getPacket();
    }

    public static byte[] showNotes(final ResultSet notes, final int count) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_NOTES.getValue());
        mplew.write(4);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(PacketHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(notes.getInt("gift"));
            notes.next();
        }

        return mplew.getPacket();
    }

    public static byte[] useChalkboard(final int charid, final String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHALKBOARD.getValue());
        mplew.writeInt(charid);
        if (msg == null || msg.length() <= 0) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }

        return mplew.getPacket();
    }

    public static byte[] OnMapTransferResult(MapleCharacter chr, byte vip, boolean delete) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 31 00 05/08 00 // You cannot go to that place.
        // 31 00 06 00 // (null) is currently difficult to locate, so the teleport will not take place.
        // 31 00 09 00 // It's the map you're currently on.
        // 31 00 0A 00 // This map is not available to enter for the list.
        // 31 00 0B 00 // Users below level 7 are not allowed to go out from Maple Island.
        mplew.writeShort(SendPacketOpcode.TROCK_LOCATIONS.getValue());
        mplew.write(delete ? 2 : 3);
        mplew.write(vip);
        if (vip == 1) {
            int[] map = chr.getRegRocks();
            for (int i = 0; i < 5; i++) {
                mplew.writeInt(map[i]);
            }
        } else if (vip == 2) {
            int[] map = chr.getRocks();
            for (int i = 0; i < 10; i++) {
                mplew.writeInt(map[i]);
            }
        } else if (vip == 3 || vip == 5) {
            int[] map = chr.getHyperRocks();
            for (int i = 0; i < 13; i++) {
                mplew.writeInt(map[i]);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getTrockMessage(byte op) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TROCK_LOCATIONS.getValue());
        mplew.writeShort((short) op);

        return mplew.getPacket();
    }

    public static void addCashItemInfo(MaplePacketLittleEndianWriter mplew, Item item, int accId, int sn) {
        addCashItemInfo(mplew, item, accId, sn, true);
    }

    public static void addCashItemInfo(MaplePacketLittleEndianWriter mplew, Item item, int accId, int sn, boolean isFirst) {
        addCashItemInfo(mplew, item.getUniqueId(), accId, item.getItemId(), sn, item.getQuantity(), item.getGiftFrom(), item.getExpiration(), isFirst); //owner for the lulz
    }

    public static void addCashItemInfo(MaplePacketLittleEndianWriter mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire) {
        addCashItemInfo(mplew, uniqueid, accId, itemid, sn, quantity, sender, expire, true);
    }

    public static void addCashItemInfo(MaplePacketLittleEndianWriter mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire, boolean isFirst) {
        mplew.writeLong(uniqueid > 0 ? uniqueid : 0);
        mplew.writeInt(accId);
        mplew.writeInt(0);
        mplew.writeInt(itemid);
        mplew.writeInt(sn);
        mplew.writeShort(quantity == 0 ? 1 : quantity);
        mplew.writeAsciiString(sender, 15); //owner for the lulzlzlzl
        addExpirationTime(mplew, expire);
        mplew.writeInt(expire == -1 ? 30 : 0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(0);
        mplew.writeLong(PacketHelper.getTime(-2L));
        mplew.writeInt(0);
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(0);
        }
    }

    public static void addCashMenuItemInfo(MaplePacketLittleEndianWriter mplew, CashItemInfo item) {
        mplew.writeInt(4000000);
        mplew.writeInt(4020000); //4000000 + 10000 + page * 10000
        mplew.writeInt(1070101); //1000000 + 70000 + page * 100 + item on page
        mplew.writeMapleAsciiString(""); //img url ends in .jpg for image
        mplew.writeInt(item.getSN());
        mplew.writeInt(item.getId());
        mplew.writeInt(1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(item.getPrice());
        mplew.writeLong(PacketHelper.getTime(item.getPeriod()));
        mplew.writeLong(PacketHelper.getTime(item.getPeriod()));
        mplew.writeLong(PacketHelper.getTime(item.getPeriod()));
        mplew.writeLong(PacketHelper.getTime(item.getPeriod()));
        mplew.writeInt(item.getPrice()); //after discount
        mplew.writeInt(0);
        mplew.writeInt(item.getCount());
        mplew.writeInt(90);
        mplew.write(1); //buy
        mplew.write(1); //gift
        mplew.write(1); //cart
        mplew.write(0);
        mplew.write(1); //favorite
        mplew.writeInt(2);
        mplew.writeInt(13337); //likes
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        List pack = CashItemFactory.getInstance().getPackageItems(item.getSN());
        if (pack == null) {
            mplew.writeInt(0);
        } else {
            mplew.writeInt(pack.size());
            for (int i = 0; i < pack.size(); i++) {
                mplew.writeInt(item.getSN()); //should be pack item sn
                mplew.writeInt(((Integer) pack.get(i)));
                mplew.writeInt(0);
                mplew.writeInt(0); //pack item usual price
                mplew.writeInt(0); //pack item discounted price
                mplew.writeInt(0);
                mplew.writeInt(1);
                mplew.writeInt(0);
                mplew.writeInt(2);
            }
        }
    }

    public static byte[] sendCSFail(int err) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(Operation_Code + 18);
        mplew.write(err);
        // 1: Request timed out.\r\nPlease try again.
        // 3: You don't have enough cash.
        // 4: You can't buy someone a cash item gift if you're under 14.
        // 5: You have exceeded the allotted limit of price\r\nfor gifts.
        // 10: Please check and see if you have exceeded\r\nthe number of cash items you can have.
        // 11: Please check and see\r\nif the name of the character is wrong,\r\nor if the item has gender restrictions.
        // 44/69: You have reached the daily maximum \r\npurchase limit for the Cash Shop.
        // 22: Due to gender restrictions, the coupon \r\nis unavailable for use.
        // 17: This coupon was already used.
        // 16: This coupon has expired.
        // 18: This coupon can only be used at\r\nNexon-affiliated Internet Cafe's.\r\nPlease use the Nexon-affiliated Internet Cafe's.
        // 19: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been used.
        // 20: This coupon is a Nexon-affiliated Internet Cafe-only coupon,\r\nand it had already been expired.
        // 14: Please check and see if \r\nthe coupon number is right.
        // 23: This coupon is only for regular items, and \r\nit's unavailable to give away as a gift.
        // 24: This coupon is only for MapleStory, and\r\nit cannot be gifted to others.
        // 25: Please check if your inventory is full or not.
        // 26: This item is only available for purchase by a user at the premium service internet cafe.
        // 27: You are sending a gift to an invalid recipient.\r\nPlease check the character name and gender.
        // 28: Please check the name of the receiver.
        // 29: Items are not available for purchase\r\n at this hour.
        // 30: The item is out of stock, and therefore\r\nnot available for sale.
        // 31: You have exceeded the spending limit of NX.
        // 32: You do not have enough mesos.
        // 33: The Cash Shop is unavailable\r\nduring the beta-test phase.\r\nWe apologize for your inconvenience.
        // 34: Check your PIC password and\r\nplease try again.
        // 37: Please verify your 2nd password and\r\ntry again.
        // 21: This is the NX coupon number.\r\nRegister your coupon at www.nexon.net.
        // 38: This coupon is only available to the users buying cash item for the first time.
        // 39: You have already applied for this.
        // 47: You have exceeded the maximum number\r\nof usage per account\for this account.\r\nPlease check the coupon for detail.
        // 49: The coupon system will be available soon.
        // 50: This item can only be used 15 days \r\nafter the account's registration.
        // 51: You do not have enough Gift Tokens \r\nin your account. Please charge your account \r\nwith Nexon Game Cards to receive \r\nGift Tokens to gift this item.
        // 52: Due to technical difficulties,\r\nthis item cannot be sent at this time.\r\nPlease try again.
        // 53: You may not gift items for \r\nit has been less than two weeks \r\nsince you first charged your account.
        // 54: Users with history of illegal activities\r\n may not gift items to others. Please make sure \r\nyour account is neither previously blocked, \r\nnor illegally charged with NX.
        // 55: Due to limitations, \r\nyou may not gift this item as this time. \r\nPlease try again later.
        // 56: You have exceeded the amount of time \r\nyou can gift items to other characters.
        // 57: This item cannot be gifted \r\ndue to technical difficulties. \r\nPlease try again later.
        // 58: You cannot transfer \r\na character under level 20.
        // 59: You cannot transfer a character \r\nto the same world it is currently in.
        // 60: You cannot transfer a character \r\ninto the new server world.
        // 61: You may not transfer out of this \r\nworld at this time.
        // 62: You cannot transfer a character into \r\na world that has no empty character slots.
        // 63: The event has either ended or\r\nthis item is not available for free testing.
        // 6: You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
        // 7: That character could not be found in this world.\r\nGifts can only be sent to character\r\nin the same world.
        // 8: This item has a gender restriction.\r\nPlease confirm the gender of the recipient.
        // 9: The gift cannot be sent because\r\nthe recipient's Inventory is full.
        // 64: This item cannot be purchased \r\nwith MaplePoints.
        // 65: Sorry for inconvinence. \r\nplease try again.
        // 67: This item cannot be\r\npurchased by anyone under 7.
        // 68: This item cannot be\r\nreceived by anyone under 7.
        // 66: You can no longer purchase or gift that Item of the Day.
        // 70: NX use is restricted.\r\nPlease change your settings in the NX Security Settings menu\r\nin the Nexon Portal My Info section.
        // 74: This item is not currently for sale.
        // 81: You have too many Cash Items.\r\nPlease clear 1 Cash slot and try again.
        // 90: You have exceeded the purchase limit for this item.\r\nYou cannot buy anymore.
        // 88: This item is non-refundable.
        // 87: Items cannot be refunded if\r\n7 days have passed from purchase.
        // 89: Refund cannot be processed, as some of the items in this\r\npackage have been used.
        // 86: Refund is currently unavailable.
        // 91: You cannot name change.\r\na character under level 10.
        // default: Due to an unknown error,\r\nthe request for Cash Shop has failed.

        return mplew.getPacket();
    }

    public static byte[] getBoosterPack(int f1, int f2, int f3) { //item IDs
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_PACK.getValue());
        mplew.write(0xD7);
        mplew.writeInt(f1);
        mplew.writeInt(f2);
        mplew.writeInt(f3);

        return mplew.getPacket();
    }

    public static byte[] getBoosterPackClick() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_PACK.getValue());
        mplew.write(0xD5);

        return mplew.getPacket();
    }

    public static byte[] getBoosterPackReveal() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_PACK.getValue());
        mplew.write(0xD6);

        return mplew.getPacket();
    }

    public static byte[] sendMesobagFailed(final boolean random) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(random ? SendPacketOpcode.R_MESOBAG_FAILURE.getValue() : SendPacketOpcode.MESOBAG_FAILURE.getValue());

        return mplew.getPacket();
    }

    public static byte[] sendMesobagSuccess(int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MESOBAG_SUCCESS.getValue());
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static byte[] sendRandomMesobagSuccess(int size, int mesos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.R_MESOBAG_SUCCESS.getValue());
        mplew.write(size); // 1 = small, 2 = adequete, 3 = large, 4 = huge
        mplew.writeInt(mesos);

        return mplew.getPacket();
    }
}
