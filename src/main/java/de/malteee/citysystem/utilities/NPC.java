package de.malteee.citysystem.utilities;

import org.bukkit.entity.Player;

public class NPC {

    public NPC(Player player) {
/*
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        UUID uuid = UUID.randomUUID();

        GameProfile gameProfile = new GameProfile(uuid, "PLAYER");

        EntityPlayer npc = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(),
                gameProfile,
                ClientInformation.a());

        npc.c = new PlayerConnection(((CraftServer) Bukkit.getServer()).getServer(),
                new NetworkManager(EnumProtocolDirection.a),
                npc,
                CommonListenerCookie.a(gameProfile, false));

        Class<ClientboundPlayerInfoUpdatePacket.a> enumClass = ClientboundPlayerInfoUpdatePacket.a.class; /*ClientboundPlayerInfoUpdatePacket.Action

        ClientboundPlayerInfoUpdatePacket.a[] enumConstants = enumClass.getEnumConstants();

        for(ClientboundPlayerInfoUpdatePacket.a constant : enumConstants){
            if(constant.name().equals("ADD_PLAYER")){
                ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(constant, npc);
                nmsPlayer.c.sendPacket(packet);

                PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(
                        npc.an(), //Entity ID
                        npc.cz(), //Entity UUID
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch(),
                        npc.am(), //Entity Type
                        0,
                        new Vec3D(0, 0, 0),
                        0
                );
                nmsPlayer.c.sendPacket(spawnPacket);


                //HEAD ROTATION / BODY POSITION CODE
                PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.an(), (byte) 180, (byte) 0, true);
                PacketPlayOutEntityHeadRotation rotationPacket = new PacketPlayOutEntityHeadRotation(npc, (byte) (player.getLocation().getYaw() * 255F / 360F));

                nmsPlayer.c.sendPacket(lookPacket);
                nmsPlayer.c.sendPacket(rotationPacket);

                break;
            }
        }*/
    }
}
